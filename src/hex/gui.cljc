(ns hex.gui
  (:require [hex.cartesian :as cartesian]
            [hex.coordinates :as c]))

(def base-vertices
  {::pointy (->> 6
                 range
                 (mapv #(->> %
                             (* (/ Math/PI 3))
                             ((juxt Math/sin Math/cos)))))
   ;; TODO implement flat vertices
   ::flat   nil})

(defn vertices
  [radius orientation]
  (->> base-vertices
       orientation
       (map (partial map (partial * radius)))))

(defn hexagon
  [hex radius border-width orientation]
  (assoc hex
         :points
         (let [hex (c/->cartesian hex radius border-width)
               cx  (::cartesian/x hex)
               cy  (::cartesian/y hex)]
           (->> orientation
                (vertices radius)
                (map (fn [[x y]] [(+ x cx) (+ y cy)]))))))


(defn honeycomb
  [radius border-width orientation hexes]
  (map #(hexagon % radius border-width orientation) hexes))
