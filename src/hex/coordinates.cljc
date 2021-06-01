(ns hex.coordinates
  #?(:cljs (:require-macros [hex.coordinates :refer [defcoord]]))
  (:require [clojure.spec.alpha :as s]
            [hex.axial :as axial]
            [hex.cube :as cube]
            [hex.cartesian :as cartesian]))

(defmacro defcoord
  [k ks bad-ks]
  `(doall (for [k# (eval ~ks)] (s/def k# int?)))
  `(letfn [(contains-any?# [m#]
                           (->> m#
                                keys
                                (some (set (eval ~bad-ks)))))]
     (s/def ~k (s/and (s/keys :req (eval ~ks)) (complement contains-any?#)))))

(defcoord ::cube-base cube/coords axial/coords)

(s/def ::cube (s/and ::cube-base #(zero? (apply + (vals %)))))

(defcoord ::axial axial/coords cube/coords)

(s/def ::grid
  (s/+ (s/alt :cubes  ::cube
              :axials ::axial)))

(def coordinate-systems (atom [::axial ::cube ::grid]))

(defn coordinate-system
  [hex]
  (cond (and (map? hex) (every? (partial contains? hex) cube/coords)) ::cube
        (and (map? hex) (every? (partial contains? hex) axial/coords)) ::axial
        (and (map? hex) (every? (partial contains? hex) cartesian/coords))
        ::cartesian
        (and (every? (complement coll?) hex) (count hex)) (count hex)
        (and (map? (first hex))
             (some (partial every? (partial contains? (first hex)))
                   [cube/coords axial/coords]))
        ::grid))

(defmulti ->cube coordinate-system)

(defmulti ->axial coordinate-system)
(defmulti ->cartesian (fn [hex & _] (coordinate-system hex)))

(defmethod ->cube ::cube [hex] hex)

(defmethod ->cube ::axial
  [{q   ::axial/q
    r   ::axial/r
    :as hex}]
  (->> hex
       (#(apply dissoc % axial/coords))
       (merge (zipmap cube/coords [q (- 0 q r) r]))))

(defmethod ->cube ::grid [hexes] (map ->cube hexes))

(defmethod ->cube :default [vecs] (mapv ->cube vecs))

(defmethod ->cube 2 [v] (->cube (zipmap axial/coords v)))

(defmethod ->cube 3 [v] (zipmap cube/coords v))

(defmethod ->axial ::axial [hex] hex)

(defmethod ->axial ::cube
  [{x   ::cube/x
    z   ::cube/z
    :as hex}]
  (->> hex
       (#(apply dissoc % cube/coords))
       (merge (zipmap axial/coords [x z]))))

(defmethod ->axial :default [vecs] (mapv ->axial vecs))

(defmethod ->axial 3 [v] (->axial (zipmap cube/coords v)))

(defmethod ->axial 2 [v] (zipmap axial/coords v))

(defn ->vectors
  [hexes]
  (->> hexes
       (map sort)
       (mapv (partial mapv second))))

(defn apothem [radius] (* radius (Math/cos (/ Math/PI 6))))

(defmethod ->cartesian ::axial
  ([{r   ::axial/r
     q   ::axial/q
     :as hex} radius border-width]
   (let [w (/ border-width 2)
         x (* (+ (* q 2) r) (+ w (apothem radius)))
         y (* r
              #?(:clj 3/2
                 :cljs 1.5)
              (+ radius (/ w (Math/sin (/ Math/PI 3)))))]
     (->> hex
          (#(apply dissoc % axial/coords))
          (merge (zipmap cartesian/coords [x y])))))
  ([hex radius] (->cartesian hex radius 0)))

(defmethod ->cartesian ::cube
  [hex & params]
  (apply ->cartesian (->axial hex) params))
