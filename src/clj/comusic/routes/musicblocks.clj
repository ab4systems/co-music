(ns comusic.routes.musicblocks
    (:require [comusic.layout :as layout]
      [compojure.core :refer [defroutes GET POST]]
      [clojure.string :as str]
      [comusic.db.core :as db]
      [struct.core :as st]
      [ring.util.http-response :as response]
      [clojure.java.io :as io]
      [comusic.sequencer :as sequencer]
      [overtone.core :as overtone]))


(defn- process-output
       [sir]
       (str/join " " (map
                       (fn [x] (if (re-matches #"\d+" x) (sequencer/midi-to-note (Integer/parseInt x)) x))
                       (str/split (str/replace (str/replace sir #"\[|\(|\]" "") #"\)" " ;") #" "))))

(defn get-blocks
      [{:keys [flash]}]
      (layout/render
        "musicblocks.html"
        (merge {:absrhythmblocks (db/get-absrhythmblocks) :harmblocks (map (fn [x] (assoc x :harmblock_block (process-output (:harmblock_block x)))) (db/get-harmblocks))}
               (select-keys flash [:name :message :errors]))))

; sa am grija la numele parametrilor
(defn save-musicblock! [{:keys [params]}]
      (do
        (try
          (db/save-musicblock!
            (assoc params :harmblock_id (Integer/parseInt (:mihai params))
                            :absrhythmblock_id (Integer/parseInt (:irina params))
                            :musicblock_block (str (apply list (sequencer/music-block (read-string (:harmblock_block (db/get-harmblock-block {:mihai (Integer/parseInt (:mihai params))})))
                                                                                      (read-string (:absrhythmblock_block (db/get-absrhythmblock-block {:irina (Integer/parseInt (:irina params))}))))))))
          (catch Exception e
            (println "este deja in BD")))
        (response/found "/musicblocks")))
