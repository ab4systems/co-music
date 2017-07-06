(ns comusic.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[comusic started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[comusic has shut down successfully]=-"))
   :middleware identity})
