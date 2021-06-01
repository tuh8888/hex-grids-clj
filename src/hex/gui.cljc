(ns hex.gui
  (:require [hex.cartesian :as cartesian]
            [hex.coordinates :as c]
            [hex.core :refer [translate]]))

(def base-vertices
  {::pointy (->> 6
                 range
                 (map (fn [i]
                        (let [c (* (/ Math/PI 3) i)]
                          {::cartesian/x (Math/sin c)
                           ::cartesian/y (Math/cos c)}))))
   ;; TODO implement flat vertices
   ::flat   nil})

(defn map-vals
  [f m]
  (->> m
       (map (juxt key (comp f val)))
       (into {})))

(defn vertices
  [radius orientation]
  (->> base-vertices
       orientation
       (map (partial map-vals (partial * radius)))))

(defn hexagon
  [hex radius border-width orientation]
  (let [hex (c/->cartesian hex radius border-width)]
    (->> orientation
         (vertices radius)
         (map (partial translate hex))
         (map #(select-keys % cartesian/coords))
         (map vals)
         (map vec))))
