(ns remix.validate
  "Validate a map with apologies to https://github.com/weavejester/valip")

(defn- validate
  ([m rule]
     (if (-> rule first coll?)
       (->> rule (map (partial validate m)) (remove nil?) first)
       (apply validate m rule)))
  ([m k pred? error] (validate m k k pred? error))
  ([m val-fn k pred? error]
     (when-not (-> m val-fn pred?)
       {k [error]})))

(defn invalid?
  "If the map m is invalid against rules, return a map of errors otherwise nil.

   A rule is vector having the following items:
     [key predicate error]
     [val-fn key predicate error]

  A rule can also be a collection containing rules and the first invalid
  rule gets into the error map.

   Error map is {key [error]}"
  [m & rules]
  (->> rules
       (map (partial validate m))
       (apply merge-with into)))
