(ns comusic.routes.home
    (:require [comusic.layout :as layout]
      [compojure.core :refer [defroutes GET]]
      [ring.util.http-response :as response]
      [clojure.java.io :as io]
      [overtone.core :as overtone]))

(defn home-page []
      (layout/render "home.html"
                     {:connected? (overtone/server-connected?)}))

; {:docs (-> "docs/docs.md" io/resource slurp)}

(defn about-page []
        (layout/render "about.html"))

(defn music []
      (if (overtone/server-connected?)
        (layout/render "music.html")
        "Trebuie conectat Supercollider!"))

(defn connect []
      (do
        (if (overtone/server-disconnected?)
          (overtone/boot-external-server))
        (layout/render " home.html ")))


