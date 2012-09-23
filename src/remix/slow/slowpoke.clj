(ns remix.slow.slowpoke
  "Slowpoke does nothing except take a long time to load.
   If remix.test.rhandler is a loaded lib, then it sleeps
   for 2 seconds otherwise 45.")

(let [sleep-in-sec (if (contains? (loaded-libs) 'remix.test.rhandler) 2 45)]
  (println "remix.slow.slowpoke: sleeping for" sleep-in-sec "seconds.")
  (Thread/sleep (* sleep-in-sec 1000))
  (println "remix.slow.slowpoke: done sleeping."))