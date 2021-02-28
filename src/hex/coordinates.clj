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
  (or (->> @coordinate-systems
        (filter #(s/valid? % hex))
        first)
    (and (every? (complement coll?) hex)
      (count hex))))

(defmulti ->cube coordinate-system)

(defmulti ->axial coordinate-system)

(defmethod ->cube ::cube [hex] hex)

(defmethod ->cube ::axial
  [{q ::axial/q r ::axial/r :as hex}]
  (-> hex
    (dissoc ::axial/q ::axial/r)
    (assoc ::cube/x q ::cube/y (- 0 q r) ::cube/z r)))

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
  (-> hex
    (dissoc ::cube/x ::cube/y ::cube/z)
    (assoc ::axial/q x ::axial/r z)))

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


(->> [#:hex.cube{:x 1 :y 0 :z -1}
      #:hex.cube{:x 0 :y 1 :z -1}
      #:hex.cube{:x -1 :y 1 :z 0}
      #:hex.cube{:x -1 :y 0 :z 1}
      #:hex.cube{:x 0 :y -1 :z 1}
      #:hex.cube{:x 1 :y -1 :z 0}]
  (map sort)
  (mapv (partial mapv second)))
