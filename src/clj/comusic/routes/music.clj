(ns comusic.routes.music
    (:require [comusic.layout :as layout]
      [compojure.core :refer [defroutes GET POST]]
      [clojure.string :as str]
      [comusic.db.core :as db]
      [struct.core :as st]
      [ring.util.http-response :as response]
      [clojure.java.io :as io]
      [comusic.sequencer :as sequencer]
      [overtone.core :as overtone]))

;; PROBLEMA: CAND PORNESC SERVERUL (lein run)
;; TREBUIE SA COMENTEZ TOT CODUL OVERTONE PENTRU CA LA COMPILARE DA EROARE
;; DACA NU E CONECTAT SUPERCOLLIDER-UL, NU STIU CUM SA REZOLV


(defn canta [note]
      (do
        ;;(sequencer/play-note-as-midi sequencer/bell note)
        (layout/render "music.html")
        ))

(defn stop []
      (do
         (overtone/stop)
        (layout/render "music.html")))

;; ceva probleme cu "", ciudat


;; {:keys [flash]}
;;(merge {:harmblocks (db/get-harmblocks)}
;;(select-keys flash [:harmblock_id :harmblock_block]))


