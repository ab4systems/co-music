(ns comusic.routes.absrhythmblocks
    (:require [comusic.layout :as layout]
      [compojure.core :refer [defroutes GET POST]]
      [clojure.string :as str]
      [comusic.db.core :as db]
      [struct.core :as st]
      [ring.util.http-response :as response]
      [clojure.java.io :as io]
      [comusic.sequencer :as sequencer]
      [overtone.core :as overtone]))


(defn- process-input [sir]
       sir)
;; TODO: PROCESS-INPUT

(defn save-absrhythmblock! [{:keys [params]}]
      (do
        (db/save-absrhythmblock!
          (assoc params :absrhythmblock_block (str (process-input (:absrhythmblock_block params)))))
        (response/found "/abstractrhythmblocks")))

(defn delete-absrhythmblock! [{:keys [params]}]
      (do
        (try
          (db/delete-absrhythmblock! (assoc params :absrhythmblock_id (Integer/parseInt (:absrhythmblock_id params))))
          (catch Exception e
            (println "se foloseste rhyhtmblockul asta")))
        (response/found "/abstractrhythmblocks")))

(defn get-absrhythmblocks
      [{:keys [flash]}]
      (layout/render
        "absrhythmblocks.html"
        (merge {:absrhythmblocks (db/get-absrhythmblocks)}
               (select-keys flash [:name :message :errors]))))