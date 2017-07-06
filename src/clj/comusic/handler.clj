(ns comusic.handler
  (:require [compojure.core :refer [defroutes routes wrap-routes]]
            [comusic.layout :refer [error-page]]
            [compojure.core :refer [defroutes GET POST DELETE]]
            [comusic.routes.home :as home-routes]
            [comusic.routes.music :as music-routes]
            [comusic.routes.harmblocks :as harmblocks-routes]
            [comusic.routes.absrhythmblocks :as absrhythmblocks-routes]
            [comusic.routes.musicblocks :as musicblocks-routes]
            [comusic.routes.song :as song-routes]
            [compojure.route :as route]
            [comusic.env :refer [defaults]]
            [mount.core :as mount]
            [comusic.middleware :as middleware]))

(mount/defstate init-app
                :start ((or (:init defaults) identity))
                :stop  ((or (:stop defaults) identity)))

(defroutes all-routes
           (GET "/" [] (home-routes/home-page))
           (GET "/about" [] (home-routes/about-page))
           (GET "/connect" [] (home-routes/connect))
           (GET "/music/canta" [note] (music-routes/canta note))
           (GET "/music/stop" [] (music-routes/stop))
           (POST "/harmonicblocks" request (harmblocks-routes/save-harmblock! request))
           (GET "/harmonicblocks" request (harmblocks-routes/get-harmblocks request))
           (POST "/harmonicblocks/delete/:harmblock_id" request (harmblocks-routes/delete-harmblock! request))
           (GET "/abstractrhythmblocks" request (absrhythmblocks-routes/get-absrhythmblocks request))
           (POST "/abstractrhythmblocks" request (absrhythmblocks-routes/save-absrhythmblock! request))
           (POST "/abstractrhythmblocks/delete/:absrhythmblock_id" request (absrhythmblocks-routes/delete-absrhythmblock! request))
           (GET "/musicblocks" request (musicblocks-routes/get-blocks request))
           (POST "/musicblocks/add" request (musicblocks-routes/save-musicblock! request))
           (GET "/song" request (song-routes/get-musicblocks request))
           (POST "/song/delete/:harmblock_id/:absrhythmblock_id" request (song-routes/delete-musicblock! request))
           (POST "/song/play/:harmblock_id/:absrhythmblock_id" request (song-routes/play-musicblock request))
           (POST "/song/addtrack" request (song-routes/add-track request))
           (POST "/song/playsong" request (song-routes/play-song request))
           (POST "/song/stopsong" request (song-routes/stop-song request))
           (POST "/song/changebpm" request (song-routes/change-bpm request))
           (POST "/song/erasesong" request (song-routes/erase-song request))
           (POST "/song/addtotrack" request (song-routes/add-to-track request)))

(def app-routes
  (routes
    (->
         all-routes
        (wrap-routes middleware/wrap-csrf)
        (wrap-routes middleware/wrap-formats))
    (route/not-found
      (:body
        (error-page {:status 404
                     :title "page not found"})))))


(defn app [] (middleware/wrap-base #'app-routes))
