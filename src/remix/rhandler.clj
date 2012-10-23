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

(defn- dispatch-request [wrapped-handler req]
  (if-let [resp (some (fn [[_ h]] (h req)) @routes)]
    resp
    (wrapped-handler req)))

(defn wrap-rhandler
  "Middleware that dispatches requests to handlers defined by defrh.
   If there is no matching defrh handler, the handler argument is called.

   ns-prefixes are optional strings. All namespaces for these
   prefixes are required. If load-handler, a ring handler,
   is not nil, the namespaces will be required in a future and the
   load-handler will handle requests until they are all loaded."
  ([handler] (wrap-rhandler handler nil))
  ([handler load-handler & ns-prefixes]
     (if load-handler
       (let [f (future (require-namespaces ns-prefixes))]
         (fn [req]
           (if (and (realized? f) @f)
             (dispatch-request handler req)
             (load-handler req))))
       (do
         (require-namespaces ns-prefixes)
         (partial dispatch-request handler)))))

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
         (swap! routes assoc ~k (~method ~route ~bindings #(~fn-name %)))
         nil)
      `(do
         (swap! routes assoc ~k (~method ~route ~bindings ~@body))
         nil))))
