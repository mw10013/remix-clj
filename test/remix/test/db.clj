(ns remix.test.db
  (:use clojure.test)
  (:require [clojure.java.jdbc :as jdbc]
            [remix [sql :as sql] [db :as db]]))

(deftest create
  (let [db (db/create-db {:datasource-spec {:classname "org.hsqldb.jdbcDriver"
                                             :subprotocol "hsqldb"
                                             :subname "remix_test_hsqldb"}})]
    (is db)
    (db/destroy-db db))
  (let [db (db/create-db {:datasource-spec "hsql//localhost/remix_test_hsqldb"})]
    (is db)
    (db/destroy-db db))
  (let [db (db/create-db {:datasource-spec "jdbc:hsql//localhost/remix_test_hsqldb"})]
    (is db)
    (db/destroy-db db)))

