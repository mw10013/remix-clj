(ns remix.middleware.nested-params
  "Convert a single-depth map of parameters to a nested map.
   Extends nested key syntax of ring's nested-params
   (https://github.com/ring-clojure/ring)")

(defn parse-nested-keys
  "Parse a parameter name into a list of keys using a 'C'-like index
  notation. e.g.
    \"foo[bar][][baz]\"
    => [\"foo\" \"bar\" \"\" \"baz\"]

   If the parameter name starts with '[', it will be passed to read-string
   to parse into a vector. The vector may contain numbers which will be
   treated as indexes into nested vectors. e.g.
     \"[:parents 1 :children 0 :name]\""
  [param-name]
  (if (= (first param-name) \[)
    (binding [*read-eval* false] (read-string param-name))
    (let [[_ k ks] (re-matches #"(.*?)((?:\[.*?\])*)" (name param-name))
          keys     (if ks (map second (re-seq #"\[(.*?)\]" ks)))]
      (cons k keys))))

(defn- assoc-nested
  "Similar to assoc-in, but treats values of blank keys as elements in a
  list. Numbers are treated as indexes into nested vectors."
  [m [k & ks] v]
  (conj m
        (if k
          (if-let [[j & js] ks]
            (cond
             (number? j) (let [nested-vec (get m k [])
                               nested-cnt (count nested-vec)
                               nested-vec (if (< j nested-cnt)
                                            nested-vec
                                            (into nested-vec (repeat (inc (- j nested-cnt)) nil)))
                               nested-vec (update-in nested-vec [j] (fnil assoc-nested {}) js v)]
                           {k nested-vec})
             (= j "") {k (assoc-nested (get m k []) js v)}
             :else {k (assoc-nested (get m k {}) ks v)})
            {k v})
          v)))

(defn- param-pairs
  "Return a list of name-value pairs for a parameter map."
  [params]
  (mapcat
    (fn [[name value]]
      (if (sequential? value)
        (for [v value] [name v])
        [[name value]]))
    params))

(defn- nest-params
  "Takes a flat map of parameters and turns it into a nested map of
  parameters, using the function parse to split the parameter names
  into keys."
  [params parse]
  (reduce
    (fn [m [k v]]
      (assoc-nested m (parse k) v))
    {}
    (param-pairs params)))

(defn wrap-nested-params
  "Middleware to converts a flat map of parameters into a nested map.

  Uses the function in the :key-parser option to convert parameter names
  to a list of keys. Values in keys that are empty strings are treated
  as elements in a list. Defaults to using the parse-nested-keys function.

  e.g.
    {\"foo[bar]\" \"baz\"}
    => {\"foo\" {\"bar\" \"baz\"}}

    {\"foo[]\" \"bar\"}
    => {\"foo\" [\"bar\"]}

  Extends ring's nested key syntax by accepting parameter names as vectors of keys.
  Keys may be keywords or integers, which will be treated as indexes into nested vectors.
  If any level does exit, hash-maps and vectors will be created.

  e.g.
    {\"[:as 1 :bs 1 :cs 0 :id]\" \"1\"}
    => {:as [nil {:bs [nil {:cs [{:id \"1\"}]}]}]}"
  [handler & [opts]]
  (fn [request]
    (let [parse   (:key-parser opts parse-nested-keys)
          request (update-in request [:params] nest-params parse)]
      (handler request))))
