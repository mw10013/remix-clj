(ns remix.test.bootstrap
  (:require [remix.bootstrap :as bs])
  (:use clojure.test
        [hiccup [form :only [submit-button text-field]]]))

(deftest alert
  (is (= (bs/alert "abacab")
         [:div {:class "alert "} [:button.close {:data-dismiss :alert, :type :button} "x"] "abacab"]))
  (is (= (bs/alert {:class :alert-error} "abacab")
         [:div {:class "alert alert-error"} [:button.close {:data-dismiss :alert, :type :button} "x"] "abacab"]))
  (is (= (bs/alert [:h1 "abacab"])
         [:div {:class "alert "} [:button.close {:data-dismiss :alert, :type :button} "x"] [:h1 "abacab"]]))
  (is (= (bs/alert {:class :alert-error} [:h1 "abacab"])
         [:div {:class "alert alert-error"} [:button.close {:data-dismiss :alert, :type :button} "x"] [:h1 "abacab"]]))
  (is (= (bs/alert {:class :alert-error} [:h1 "abacab"] [:p "more abacab"])
         [:div {:class "alert alert-error"} [:button.close {:data-dismiss :alert, :type :button} "x"] [:h1 "abacab"] [:p "more abacab"]])))

(deftest control-group
  (is (= (bs/control-group :label1 "Label1" {:label1 ["Required."]} (text-field :label1 nil))
         [:div.control-group.error
          [:label {:class :control-label, :for "label1"} "Label1"]
          [:div.controls "<input id=\"label1\" name=\"label1\" type=\"text\" />"
           [:span.help-inline "Required."] nil]])))

(deftest control-actions
  (is (= (bs/control-actions (submit-button {:class "btn btn-primary"} "Submit"))
         [:div.control-group [:div.controls "<input class=\"btn btn-primary\" type=\"submit\" value=\"Submit\" />"]])))

