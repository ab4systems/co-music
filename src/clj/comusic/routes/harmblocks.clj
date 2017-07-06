(ns comusic.routes.harmblocks
    (:require [comusic.layout :as layout]
      [compojure.core :refer [defroutes GET POST]]
      [clojure.string :as str]
      [comusic.db.core :as db]
      [struct.core :as st]
      [ring.util.http-response :as response]
      [clojure.java.io :as io]
      [comusic.sequencer :as sequencer]
      [overtone.core :as overtone]))


(defn- process-input
       [sir]
       (apply vector (map (fn [x] (map overtone/note (filter #(not= "" %) x)))
                          (map #(str/split % #" ") (str/split sir #"[;]")))))

(defn- process-output
      [sir]
       (str/join " " (map
                          (fn [x] (if (re-matches #"\d+" x) (sequencer/midi-to-note (Integer/parseInt x)) x))
                          (str/split (str/replace (str/replace sir #"\[|\(|\]" "") #"\)" " ;") #" "))))

(defn save-harmblock! [{:keys [params]}]
      (do
        (db/save-harmblock!
          (assoc params :harmblock_block (str (process-input (:harmblock_block params)))))
        (response/found "/harmonicblocks")))

(defn delete-harmblock! [{:keys [params]}]
      (do
        (try
          (db/delete-harmblock! (assoc params :harmblock_id (Integer/parseInt (:harmblock_id params))))
          (catch Exception e
             (println "se foloseste asta")))
        (response/found "/harmonicblocks")))

;;  cred ca ar fi mai ok daca as valida inputul sa fie de forma aia, si in baza de date retin exact ce a introdus userul
;; iar pentru partea de cantat procesez sirul

(defn get-harmblocks
      [{:keys [flash]}]
      (layout/render
          "harmblocks.html"
          (merge {:harmblocks (map (fn [x] (assoc x :harmblock_block (process-output (:harmblock_block x)))) (db/get-harmblocks))}
                 (select-keys flash [:name :message :errors]))))
