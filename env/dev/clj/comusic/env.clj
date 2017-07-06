(ns comusic.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [comusic.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[comusic started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[comusic has shut down successfully]=-"))
   :middleware wrap-dev})
