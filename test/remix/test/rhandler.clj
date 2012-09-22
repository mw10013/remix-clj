(ns remix.test.rhandler
  (:require [remix.rhandler :as rh])
  (:use clojure.test
        [ring.mock.request :only [request]]))

(deftest rh
  (reset! rh/routes {})
  (let [handler (rh/wrap-rhandler identity)]
    (rh/defrh "/no-name-no-method" [] "no-name-no-method")
    (rh/defrh :post "/no-name" [] "no-name")
    (rh/defrh name-no-method "/name-no-method" [] "name-no-method")
    (rh/defrh name-and-method :post "/name-and-method" [] "name-and-method")
    (is (= 4 (count @rh/routes)))
    (is (= "no-name-no-method" (:body (handler (request :get "/no-name-no-method")))))
    (is (= "no-name" (:body (handler (request :post "/no-name")))))
    (is (= "name-no-method" (:body (handler (request :get "/name-no-method")))))
    (is (= "name-and-method" (:body (handler (request :post "/name-and-method")))))
    (is (= "name-no-method" (name-no-method {})))
    (is (= "name-and-method" (name-and-method {})))))

(deftest redefine-rh
  (reset! rh/routes {})
  (let [handler (rh/wrap-rhandler identity)]
    (rh/defrh "/get" [] "get")
    (is (= 1 (count @rh/routes)))
    (is (= "get" (:body (handler (request :get "/get")))))
    (rh/defrh "/get" [] "get redefined")
    (is (= 1 (count @rh/routes)))
    (is (= "get redefined" (:body (handler (request :get "/get")))))))

(deftest post-and-named-get-rh
  (reset! rh/routes {})
  (let [handler (rh/wrap-rhandler identity)]
    (rh/defrh rh-handler "/rh-handler" [] "rh-handler")
    (rh/defrh :post "/rh-handler" {:as req} (rh-handler req))
    (is (= 2 (count @rh/routes)))
    (is (= "rh-handler" (:body (handler (request :get "/rh-handler")))))
    (is (= "rh-handler" (:body (handler (request :post "/rh-handler")))))))

(deftest regex-rh
  (reset! rh/routes {})
  (let [handler (rh/wrap-rhandler identity)]
    (rh/defrh rh-handler ["/rh-handler/:id"  :id #"\d+"] [id] (str "rh-handler: " id))
    (is (= 1 (count @rh/routes)))
    (is (= "rh-handler: 1" (:body (handler (request :get "/rh-handler/1")))))
    (rh/defrh rh-handler ["/rh-handler/:id"  :id #"\d+"] [id] (str "rh-handler-redefined: " id))
    (is (= 1 (count @rh/routes)))
    (is (= "rh-handler-redefined: 1" (:body (handler (request :get "/rh-handler/1")))))))

(deftest rh-ns-prefix
  (reset! rh/routes {})
  (let [require-handler (fn [_] "require-handler")
        handler (rh/wrap-rhandler identity  require-handler "remix.slow")]
    (is (= 0 (count @rh/routes)))
    (is (= "require-handler" (handler {})))
    (Thread/sleep 4000)    
    (require 'remix.slow.slowpoke :verbose)
    (is (= 0 (count @rh/routes)))
    (is (= {} (handler {})))))

(defn test-ns-hook []
  (rh-ns-prefix)
  (rh)
  (redefine-rh)
  (post-and-named-get-rh)
  (regex-rh))

;(run-tests 'remix.test.rhandler)