 (ns instruments
     (:use [overtone.core]))

(definst bell [freq 440 attack 0 sustain 1 release 1]
         (let [fund freq
               overt1 (* 2.3 freq)
               overt2 (* 3.1 freq)]
              (pan2 (* (env-gen (lin attack 0 sustain 1 release 1))
                       (+ (* (sin-osc fund) 1)
                          (* (sin-osc overt1) 0.5)
                          (* (sin-osc overt2) 0.2))))))
