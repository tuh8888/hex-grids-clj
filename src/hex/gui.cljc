(ns hex.gui
  (:require [hex.axial :as axial]))

(def base-vertices
  {::pointy (->> 6
                 range
                 (map #(->> %
                            (* (/ Math/PI 3))
                            ((juxt Math/sin Math/cos)))))
   ;; TODO implement flat vertices
   ::flat   nil})

(defn vertices
  [radius orientation]
  (->> base-vertices
       orientation
       (map (partial map (partial * radius)))))

(defn apothem [radius] (* radius (Math/cos (/ Math/PI 6))))

(defn ->cartesian
  ([hex radius border-width]
   (let [{r ::axial/r
          q ::axial/q}
         hex
         w (/ border-width 2)]
     [(* (+ (* q 2) r) (+ w (apothem radius)))
      (* r
         #?(:clj 3/2
            :cljs 1.5)
         (+ radius (/ w (Math/sin (/ Math/PI 3)))))]))
  ([hex radius] (->cartesian hex radius 0)))
