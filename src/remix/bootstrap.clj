(ns remix.bootstrap
  "Functions for twitter bootstrap."
  (:use [hiccup [core :only [html]] [def :only [defhtml]] [form :only [label]]]))

(defhtml alert [m]
  (let [m (if (string? m) {:content m :class :alert-info} m)]
    [:div {:class (->> m :class name (str "alert "))}
     (:content m)]))

(defmacro control-group [k label errors & body]
  `[(if (~k ~errors) :div.control-group.error :div.control-group)
    (label {:class :control-label :for ~k} ~k ~label)
    [:div.controls
     ~@body
     (when (~k ~errors) [:span.help-inline (join \space (~k ~errors))])]])

(defn control-error
  "Takes coll of errors and joins them together in inline help."
  [coll]
  (when (not-empty coll) [:span.help-inline (clojure.string/join \space coll)]))
 
(defn control-group*
  "Takes html for label, control, and help along with error coll
   and returns html for control group."
  [label-html control-html help-html error-coll]
  [(if (empty? error-coll) :div.control-group :div.control-group.error)
   label-html
   [:div.controls
    control-html
    (control-error error-coll)
    help-html]])
 
(defn control-group-label [k text]
  (label {:class :control-label} k text))
 
(defmacro control-group
  "Returns html for a control group."
  [k label error-map & body]
  `(control-group*
    (control-group-label ~k ~label)
    (html ~@body) nil (~k ~error-map)))
 
(defmacro control-actions
  "Return html for control actions eg. Submit button."
  [& body]
  `[:div.control-group [:div.controls (html ~@body)]])