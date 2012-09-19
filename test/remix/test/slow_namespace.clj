(ns remix.test.slow-namespace
  "namespace that is slow to load for testing rhandler.")

(Thread/sleep 2000)

