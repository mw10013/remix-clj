(ns remix.rhandler
  (:use [clojure.string :only [upper-case]]
        [bultitude.core :only [namespaces-on-classpath]])
  (:require compojure.core))

(defonce ^{:doc "Routes and handlers defined by defrh."} routes (atom {}))

(defn- require-namespaces [prefixes]
  (doseq [prefix prefixes
          ns (namespaces-on-classpath :prefix prefix)]
    (require ns))
  true)

(defn wrap-rhandler
  "Middleware that dispatches requests to handlers defined by defrh.
   If there is no matching handler, the handler argument is called.

   ns-prefix is an optional string. All namespaces with that prefix
   will be required by a future. While the future is not yet realized,
   an optional require-handler will be called."
  ([handler] (wrap-rhandler handler (constantly nil)))
  ([handler require-handler & ns-prefixes]
     (if require-handler
       (let [f (future (require-namespaces ns-prefixes))]
         (fn [req]
           (if (and (realized? f) @f)
             (if-let [resp (some (fn [[_ h]] (h req)) @routes)]
               resp
               (handler req))
             (require-handler req))))
       (do
         (require-namespaces ns-prefixes)
         (fn [req]
           (if-let [resp (some (fn [[_ h]] (h req)) @routes)]
               resp
               (handler req)))))))

(defn route->key [method route]
  (->> (if (string? route) [route] route) (list* method) (clojure.string/join \space)))

(defmacro defrh
  "Adds a route handler to be dispatched by wrap-rhandler.
   
   Supported forms:

   (defrh \"/foo/:id\" [id]) an unnamed route
   (defrh :post \"foo/:id\" [id]) a route that responds to POST
   (defrh foo \"/foo/:id\" [id]) a named route
   (defrh foo :post \"/foo/:id\" [id]) 

   The default method is :get"
  [& args]
  (let [[fn-name method route bindings & body] (cond
                                                (-> args second keyword?) args
                                                (-> args first symbol?) (list* (first args) :get (rest args))
                                                (-> args first keyword?) (list* nil args)
                                                :else (list* nil :get args))
        k (route->key method route)
        method (symbol "compojure.core" (-> method name upper-case))]
    (if fn-name
      `(do
         (defn ~fn-name [req#] (compojure.core/let-request [~bindings req#] ~@body))
         (swap! routes assoc ~k (~method ~route ~bindings #(~fn-name %))))
      `(swap! routes assoc ~k (~method ~route ~bindings ~@body)))))
