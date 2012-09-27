(defproject org.clojars.mw10013/remix "0.0.5"
  :description "Mix and match machinery for web and sql."
  :url "https://github.com/mw10013/remix"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"
            :distribution :repo
            :comments "same as Clojure"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/core.incubator "0.1.1"]
                 [org.clojure/java.jdbc "0.2.2"]
                 [c3p0/c3p0 "0.9.1.2"]
                 [compojure "1.1.1"]
                 [hiccup "1.0.0"]
                 [org.clojure/data.xml "0.0.6"]
                 [bultitude "0.1.7"]]
  :min-lein-version "2.0.0"
  :profiles {:dev {:dependencies [[org.hsqldb/hsqldb "2.2.8"] [ring-mock "0.1.3"]]}
             :1.3 {:dependencies [[org.clojure/clojure "1.3.0"]]}
             :1.5 {:repositories [["sonatype-snapshots" "https://oss.sonatype.org/content/repositories/snapshots/"]]
                   :dependencies [[org.clojure/clojure "1.5.0-master-SNAPSHOT"]]}}
  :aliases {"test-all" ["with-profile" "dev,default:dev,1.3,default:dev,1.5,default" "test"]})