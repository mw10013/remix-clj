(ns remix.slowpoke)

(println "remix.slowpoke: sleeping")
(Thread/sleep (* 3 1000))
(println "remix.slowpoke: done")