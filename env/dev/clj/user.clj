(ns user
  (:require [mount.core :as mount]
            comusic.core))

(defn start []
  (mount/start-without #'comusic.core/http-server
                       #'comusic.core/repl-server))

(defn stop []
  (mount/stop-except #'comusic.core/http-server
                     #'comusic.core/repl-server))

(defn restart []
  (stop)
  (start))


