(ns remix.slow.slowpoke)

(println "remix.slow.slowpoke: sleeping")
(Thread/sleep (* 2 1000))
(println "remix.slow.slowpoke: done")