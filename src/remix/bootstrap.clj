(ns remix.bootstrap
  "Functions for twitter bootstrap."
  (:use [hiccup [core :only [html]] [def :only [defhtml defelem]] [form :only [label]]]))

(defelem alert- [& content]
  (->> content
       (concat [:div {} ; empty map needed as placeholder
                [:button.close {:type :button :data-dismiss :alert} "x"]])
       vec))

(defhtml alert
  "Return alert html for content. The first arg may be a map of attributes."
  [& content]
  (update-in (apply alert- content) [1 :class] (fnil (comp (partial str "alert ") name) "")))

(defn alert
  "Return alert html for content. The first arg may be a map of attributes."
  [& content]
  (update-in (apply alert- content) [1 :class] (fnil (comp (partial str "alert ") name) "")))

(defn control-error
  "Take coll of errors and join them together in inline help."
  [errors]
  (when (not-empty errors) [:span.help-inline (clojure.string/join \space errors)]))
 
(defn control-group*
  "Take hiccup for label, control, and help along with coll of errors
   and return hiccup for control group."
  [label control help errors]
  [(if (empty? errors) :div.control-group :div.control-group.error)
   label
   [:div.controls
    control
    (control-error errors)
    help]])
 
(defn control-group-label
  "Return control-label hiccup for text."
  [k text]
  (label {:class :control-label} k text))
 
(defmacro control-group
  "Return hiccup for a control group with a control-label label.
   Errors is a map. If k has an entry in errors, the corresponding errors are
   displayed as help-inline."
  [k label errors & body]
  `(control-group*
    (control-group-label ~k ~label)
    (html ~@body) nil (~k ~errors)))
 
(defmacro control-actions
  "Return hiccup for control actions eg. Submit button."
  [& body]
  `[:div.control-group [:div.controls (html ~@body)]])