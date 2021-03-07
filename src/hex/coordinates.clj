(ns hex.coordinates
  (:require [clojure.spec.alpha :as s]
            [hex.axial :as axial]
            [hex.cube :as cube]
            [quil.core :as q]))

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
  (cond (and (map? hex) (every? (partial contains? hex) cube/coords))                   ::cube
        (and (map? hex) (every? (partial contains? hex) axial/coords))                  ::axial
        (and (every? (complement coll?) hex) (count hex))                         (count hex)
        (and (map? (first hex))
          (some (partial every? (partial contains? (first hex))) [cube/coords axial/coords])) ::grid))

(defmulti ->cube coordinate-system)

(defmulti ->axial coordinate-system)

(defmethod ->cube ::cube [hex] hex)

(defmethod ->cube ::axial
  [{q ::axial/q r ::axial/r :as hex}]
  (->> hex
    (#(apply dissoc % axial/coords))
    (merge (zipmap cube/coords [q (- 0 q r) r]))))

(defmethod ->cube ::grid [hexes] (map ->cube hexes))

(defmethod ->cube :default [vecs]
  (mapv ->cube vecs))

(defmethod ->cube 2 [v]
  (->cube (zipmap axial/coords v)))

(defmethod ->cube 3 [v]
  (zipmap cube/coords v))

(defmethod ->axial ::axial [hex] hex)

(defmethod ->axial ::cube
  [{x ::cube/x z ::cube/z :as hex}]
  (->> hex
    (#(apply dissoc % cube/coords))
    (merge (zipmap axial/coords [x z]))))

(defmethod ->axial :default [vecs]
  (mapv ->axial vecs))

(defmethod ->axial 3 [v]
  (->axial (zipmap cube/coords v)))

(defmethod ->axial 2 [v]
  (zipmap axial/coords v))

(defn ->vectors
  [hexes]
  (->> hexes
    (map sort)
    (mapv (partial mapv second))))

(defn apothem [radius]
  (* radius (q/cos (/ q/PI 6))))

(defn ->cartesian
  [radius hex]
  (let [{r ::axial/r q ::axial/q} hex]
    [(* (+ (* q 2) r) (apothem radius))
     (* r (+ radius (/ radius 2)))]))
