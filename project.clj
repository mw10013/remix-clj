(defproject remix "0.0.4"
  :description "Mix and match machinery for web and sql."
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [org.clojure/core.incubator "0.1.1"]
                 [org.clojure/java.jdbc "0.2.2"]
                 [c3p0/c3p0 "0.9.1.2"]
                 [compojure "1.1.1"]
                 [hiccup "1.0.0"]
                 [org.clojure/data.xml "0.0.6"]
                 [bultitude "0.1.7"]]
  :dev-dependencies [[org.hsqldb/hsqldb "2.2.8"]
                     [ring-mock "0.1.3"]])