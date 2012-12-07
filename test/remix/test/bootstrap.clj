(ns remix.test.bootstrap
  (:require [remix.bootstrap :as bs])
  (:use clojure.test))

(deftest alert
  (is (= "<div class=\"alert \"><button class=\"close\" data-dismiss=\"alert\" type=\"button\">x</button>abacab</div>"
         (bs/alert "abacab")))
  (is (= "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\" type=\"button\">x</button>abacab</div>"
         (bs/alert {:class :alert-error} "abacab")))
  (is (= "<div class=\"alert \"><button class=\"close\" data-dismiss=\"alert\" type=\"button\">x</button><h1>abacab</h1></div>"
         (bs/alert [:h1 "abacab"])))
  (is (= "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\" type=\"button\">x</button><h1>abacab</h1></div>"
         (bs/alert {:class :alert-error} [:h1 "abacab"])))
  (is (= "<div class=\"alert alert-error\"><button class=\"close\" data-dismiss=\"alert\" type=\"button\">x</button><h1>abacab</h1><p>more abacab</p></div>"
         (bs/alert {:class :alert-error} [:h1 "abacab"] [:p "more abacab"]))))

