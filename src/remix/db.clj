(ns remix.db
  (:require [clojure.string :as str] [clojure.java.jdbc :as jdbc])
  (:import java.net.URI com.mchange.v2.c3p0.ComboPooledDataSource))

(def ^{:private true :doc "Map of schemes to subprotocols"} subprotocols
  {"postgres" "postgresql"})

(defn- parse-properties-uri
  "From https://github.com/clojure/java.jdbc/blob/master/src/main/clojure/clojure/java/jdbc.clj"
  [^URI uri]
  (let [host (.getHost uri)
        port (if (pos? (.getPort uri)) (.getPort uri))
        path (.getPath uri)
        scheme (.getScheme uri)]
    (merge
     {:subname (if port
                 (str "//" host ":" port path)
                 (str "//" host path))
      :subprotocol (subprotocols scheme scheme)}
     (if-let [user-info (.getUserInfo uri)]
             {:user (first (str/split user-info #":"))
              :password (second (str/split user-info #":"))}))))

(defn- strip-jdbc
  "From https://github.com/clojure/java.jdbc/blob/master/src/main/clojure/clojure/java/jdbc.clj"
  [^String spec]
  (if (.startsWith spec "jdbc:")
    (.substring spec 5)
    spec))

(defn- prepare-datasource-spec
  [{:keys [factory
           connection-uri
           classname subprotocol subname
           datasource username password
           name environment]
    :as spec}]
  (cond
    (instance? URI spec)
    (parse-properties-uri spec)
    
    (string? spec)
    (prepare-datasource-spec (URI. (strip-jdbc spec)))

    (map? spec)
    spec

    :else
    (let [^String msg (format "datasource-spec %s is missing a required parameter" spec)]
      (throw (IllegalArgumentException. msg)))))

(defn create-db [{:keys [datasource-spec pool-spec naming-strategy]}]
  (let [datasource-spec (prepare-datasource-spec datasource-spec)
        cpds (doto (ComboPooledDataSource.)
               (.setDriverClass (:classname datasource-spec))
               (.setJdbcUrl (str "jdbc:" (:subprotocol datasource-spec) ":" (:subname datasource-spec)))
               (.setUser (:user datasource-spec))
               (.setPassword (:password datasource-spec))
               (.setMaxIdleTimeExcessConnections (or (:max-idle-time-excess-in-sec pool-spec)
                                                     (* 15 60)))
               (.setMaxIdleTime (or (:max-idle-time-in-sec pool-spec) (* 30 60))))]
    {:connection-spec {:datasource cpds}
     :naming-strategy naming-strategy}))

(defn destroy-db [db]
  (when-let [datasource (-> db :connection-spec :datasource)]
    (com.mchange.v2.c3p0.DataSources/destroy datasource)))

(defn with-db*
  "Evaluates f in the context of a new/existing connection to db.
   A new connection is created within the context of the db's naming strategy."
  [db f]
  (if (jdbc/find-connection)
    (f)
    (jdbc/with-naming-strategy (:naming-strategy db)
      (jdbc/with-connection (:connection-spec db)
        (f)))))

(defmacro with-db
  "Evaluates body in the context of a new/existing connection to db."
  [db & body]
  `(with-db* ~db (fn [] ~@body)))
