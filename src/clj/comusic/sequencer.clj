(ns comusic.sequencer
    (:use [overtone.core]))


(definst bell [freq 440 amp 1]
         (let [fund freq
               overt1 (* 2 freq)
               overt2 (* 3 freq)]
              (pan2 (* amp
                       (* (env-gen (lin 0 1 0.3))
                          (+ (* (sin-osc fund) 1)
                             (* (sin-osc overt1) 0.5)
                             (* (sin-osc overt2) 0.2)))))))


(defn midi-to-note
      "returneaza sirul notei midi in notatia stiintifica"
      [midi]
      (str
        (case (mod midi 12)
            0 "C"
            1 "C#"
            2 "D"
            3 "D#"
            4 "E"
            5 "F"
            6 "F#"
            7 "G"
            8 "G#"
            9 "A"
            10 "A#"
            11 "B")
        (str (- (quot midi 12) 1))))


(defn play-note-as-midi [instr music-note vol]
      (instr (midi->hz (note music-note)) vol))

(defn play-chord [instr ch vol]
      (doseq [note ch] (play-note-as-midi instr note vol)))


;; (defn abstract-harmonic-block [& chords]
;;  (apply conj [] chords)
;;  )

;; (defn abstract-rhythm-block [& beats]
;;  (apply conj [] beats))

(defn music-block [h-block arh-block]
      (map vector h-block arh-block))

;; (def chords1 (abstract-harmonic-block (chord :C4 :minor) (chord :F4 :minor)))
;; (def rhythm1 (abstract-rhythm-block 1 3))

(def harmonic-block1 [(chord :C4 :1) (chord :G4 :1) (chord :E4 :1) (chord :F4 :1)])
(def abs-rhythm-block1 [0 2 3 6])
(def harmonic-block2 [(chord :A4 :minor) (chord :D4 :1) (chord :F4 :1) (chord :C4 :1)])
(def abs-rhythm-block2 [0 2 4 6])

;; TODO: abs-rhythm-block va fi [[mom-timp1 dur-nota1] [mom-timp2 dur-nota2] etc.]
;; (map conj [[1 1] [2 2] [3 3]] [5 6 7]]

(def music-block1 (map vector harmonic-block1 abs-rhythm-block1))
(def music-block2 (map vector harmonic-block2 abs-rhythm-block2))

(def track1 [music-block1 music-block2 music-block1])
(def track2 [music-block2 music-block1 music-block2])
(def tracktest1 [music-block1])
(def tracktest2 [music-block2])

(def song1 [track1 track2])
(def songtest [tracktest1 tracktest2])

(def metro (metronome 150))

;; schimbat for in doseq pentru ca lazy sequences + side effects = no no
;(defn play-music-block [met instr mb]
;  (let [beat (met)]
;    (doseq [v mb]
;      (at (met (+ beat (v 1))) (play-chord instr (v 0))))))

;; fac suma elementelor din abstract rhythm block
(defn sum-times-block [b]
      (reduce + (map #(nth % 1) b)))

;; ultimul element din abs-rhythm-block e ultimul moment in care apare
;; nota, si prima aparand la momentul 0 inseamna ca
;; urmatorul block ar incepe la ultim moment + lungimea notei precedente (inca nu am implementat, deci default = 2)
(defn length-block [b]
      (+ 2 ((last b) 1)))

;; (/ 60000 100) e formula (/ 60000 tempo), deci bpm -> ms
;(defn play-track [met instr tr]
;  (doseq [mb tr]
;    (play-music-block met instr mb)
;    (Thread/sleep (* (/ 60000 100) (length-block mb)))
;    ))

;; MERGE
;(defn play-song [sng]
;  (doseq [tr sng]
;    (play tr)))


(defprotocol Play
             (play [this instr vol]))

(defrecord MBlock [chord-vec]
           Play
           (play [this instr vol]
                 (let [beat (metro)]
                      (doseq [c chord-vec]
                             (at (metro (+ beat (c 1))) (play-chord instr (c 0) vol))))))
;; TODO: ar veni (... (+ beat (c 0))) (play-chord instr (c 2) (c 1)) ...)

(defrecord Track [tr]
           Play
           (play [this instr vol]
                 (let [beat (metro)]
                      (doseq [h-mb @tr]
                             (doseq [c (:music-block h-mb)]
                                    (at (metro (+ beat (:beginning h-mb) (c 1))) (play-chord instr (c 0) vol)))))))

;; de facut: sa introduca utilizatorul la ce beat sa fie introdus track-ul
(defn track
      "Constructor pentru Track"
      [mb-vec]
      (def h-tr (atom (vec (map #(hash-map :beginning 0     ;; creez atom format din vector hash-map cu fiecare block (ca sa mearga assoc-in, pentru seq nu merge), inceputul si finalul fiecarui block
                                           :end 0
                                           :music-block %) mb-vec))))
      (do
        (swap! h-tr assoc-in [0 :end] ((last (:music-block (first @h-tr))) 1)) ;; :end pentru primul hash-block din track este beat-ul la care e ultimul chord din block
        (if (> (count @h-tr) 1)                             ;; daca track-ul e facut din mai mult de un block
          (doseq [i (range 1 (count @h-tr))]                ;; pentru i de la 1 la nr de blockuri
                 (swap! h-tr assoc-in [i :beginning] (+ 1 (:end (nth @h-tr (dec i))))) ;; :beginning e :end de la hash-block[i-1] + 1
                 (swap! h-tr assoc-in [i :end] (+ (:beginning (nth @h-tr i)) ;; :end e :beginning + beat-ul la care e ultimul chord din block
                                                  ((last (:music-block (nth @h-tr i))) 1)))))
        (->Track h-tr)))

(defrecord Song [sng]
           Play
           (play [this instr vol]
                 (doseq [tr sng]
                        (play tr instr vol))))

(defn remove-mblock                                         ;; creeaza un nou Track din track-ul anterior, stergand block-ul de pe pozitia i
      "Sterge un mblock din track"
      [tr i]
      @(:tr (track (concat (subvec (vec (map :music-block @(:tr tr))) 0 i)
                     (subvec (vec (map :music-block @(:tr tr))) (inc i))))))

(defn add-mblock                                            ;; creeaza un nou track din track-ul anterior, adaugand block-ul la pozitia i NU CHIAR
      "Adauga un mblock in track"
      [tr mb i]
        @(:tr (track
          (concat (conj (subvec (vec (map :music-block @(:tr tr))) 0 i) mb)
                (subvec (vec (map :music-block @(:tr tr))) i)))))




(def mb-test (->MBlock music-block1))
(def tr-test (track track1))
(def sng-test (->Song [(track track1) (track track2) (track track1)]))
(def sng-test2 (->Song [(track tracktest1) (track tracktest2)]))


;; TODO: - validare parametri + song sa fie tot atom?
;; TODO: SEMNALE START STOP!
;; posibil reimplementare folosind https://github.com/clojure/data.finger-tree pentru track, vectorii nu sunt foarte eficienti pt adaugare/stergere in interior