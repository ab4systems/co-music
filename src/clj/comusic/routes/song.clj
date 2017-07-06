(ns comusic.routes.song
    (:require [comusic.layout :as layout]
      [compojure.core :refer [defroutes GET POST]]
      [clojure.string :as str]
      [comusic.db.core :as db]
      [struct.core :as st]
      [ring.util.http-response :as response]
      [clojure.java.io :as io]
      [comusic.sequencer :as sequencer]
      [overtone.core :as overtone]))

;; (mount/start #'<app>.core/http-server)

(def Song (atom []))

(defn get-musicblocks
      [{:keys [flash]}]
      (layout/render
        "song.html"
        (merge {:musicblocks (db/get-musicblocks)}
               (select-keys flash [:name :message :errors]))))

(defn delete-musicblock! [{:keys [params]}]
      (do
        (db/delete-musicblock! (assoc params :harmblock_id (Integer/parseInt (:harmblock_id params))
                                               :absrhythmblock_id (Integer/parseInt (:absrhythmblock_id params))))
        (response/found "/song")))

(defn play-musicblock [{:keys [params]}]
      (do
        (sequencer/play (sequencer/->MBlock (read-string (:musicblock_block (db/get-musicblock-block {:harmblock_id      (Integer/parseInt (:harmblock_id params))
                                                                                                      :absrhythmblock_id (Integer/parseInt (:absrhythmblock_id params))}))))
                        sequencer/bell 0.8)
        (response/found "/song")))

(defn add-track [{:keys [params]}]
        (try
          (swap! Song conj (sequencer/track (vector (read-string (:musicblock_block (db/get-musicblock-block {:harmblock_id (Integer/parseInt (first (str/split (:musicblockid params) #" ")))
                                                                                                  :absrhythmblock_id (Integer/parseInt (last (str/split (:musicblockid params) #" ")))}))))))
          (catch Exception e
            (println "probabil nu ai specificat corect inputul")))
        (response/found "/song"))

(defn play-song [{:keys [params]}]
      (sequencer/play (sequencer/->Song @Song) sequencer/bell 0.8)
      (response/found "/song"))

(defn stop-song [{:keys [params]}]
      (overtone/stop)
      (response/found "/song"))

(defn change-bpm [{:keys [params]}]
      (overtone/metro-bpm sequencer/metro (Integer/parseInt (:bpm params)))
      (response/found "/song"))

(defn erase-song [{:keys [params]}]
      (reset! Song [])
      (response/found "/song"))

(defn add-to-track [{:keys [params]}]
      (try
        (reset! (:tr (nth @Song (Integer/parseInt (:trackno params)))) (sequencer/add-mblock (nth @Song (Integer/parseInt (:trackno params)))
                                                                                           (read-string (:musicblock_block (db/get-musicblock-block {:harmblock_id (Integer/parseInt (first (str/split (:musicblockid params) #" ")))
                                                                                                                                                             :absrhythmblock_id (Integer/parseInt (last (str/split (:musicblockid params) #" ")))})))
                                                                                           (Integer/parseInt (:trackpos params))))
        (catch java.lang.IndexOutOfBoundsException ie
          (println "index out of bound"))
        (catch Exception e
          (println "probabil nu ai specificat corect inputul")))
      (response/found "/song"))