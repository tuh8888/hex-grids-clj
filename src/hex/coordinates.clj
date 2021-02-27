(ns hex.coordinates
  (:require [hex.axial :as axial]
            [clojure.spec.alpha :as s]
            [hex.cube :as cube]))

(defmacro defcoord
  [k ks bad-ks]
  `(doall (for [k# (eval ~ks)]
            (s/def k# int?)))
  `(letfn [(contains-any?# [m#]
             (->> m#
               keys
               (some (set (eval ~bad-ks)))))]
     (s/def ~k (s/and (s/keys :req (eval ~ks))
                 (complement contains-any?#)))))

(defcoord ::cube-base cube/coords axial/coords)

(s/def ::cube (s/and ::cube-base #(zero? (apply + (vals %)))))

(defcoord ::axial axial/coords cube/coords)

(s/def ::grid (s/+ (s/alt :cubes ::cube :axials ::axial)))

(def coordinate-systems (atom [::axial ::cube ::grid]))

(defn coordinate-system [hex]
  (->> @coordinate-systems
    (filter #(s/valid? % hex))
    first))

(defmulti ->cube coordinate-system)

(defmulti ->axial coordinate-system)

(defmethod ->cube ::cube [hex] hex)

(defmethod ->cube ::axial
  [{q ::axial/q r ::axial/r :as hex}]
  (-> hex
    (dissoc ::axial/q ::axial/r)
    (assoc ::cube/x q ::cube/y (- 0 q r) ::cube/z r)))

(defmethod ->cube ::grid [hexes] (map ->cube hexes))

(defmethod ->axial ::axial [hex] hex)

(defmethod ->axial ::cube
  [{x ::cube/x z ::cube/z :as hex}]
  (-> hex
    (dissoc ::cube/x ::cube/y ::cube/z)
    (assoc ::axial/q x ::axial/r z)))
