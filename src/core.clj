(ns core
  (:require [clojure.spec.alpha :as s]))

(defmacro defcoord
  [k ks bad-ks]
  `(doall (for [k# ~ks]
            (s/def k# int?)))
  `(letfn [(contains-any?# [m#]
             (->> m#
               keys
               (some (set ~bad-ks))))]
     (s/def ~k (s/and (s/keys :req ~ks)
                 (complement contains-any?#)))))

(def cube-coords [::x ::y ::z])
(def axial-coords [::q ::r])

(defn coordinate-system [hex]
  (->> [::axial ::cube ::grid]
    (filter #(s/valid? % hex))
    first))

(s/def ::cube (s/and (defcoord ::cube-base [::x ::y ::z] [::q ::r])
                #(zero? (apply + (vals %)))))

(defcoord ::axial [::q ::r] [::x ::y ::z])
(defmulti ->cube
  coordinate-system)

(defmethod ->cube ::cube
  [hex]
  hex)

(defmethod ->cube ::axial
  [{q ::q r ::r :as hex}]
  (let [x q
        z r
        y (- 0 x z)]
    (-> hex
      (dissoc ::q ::r)
      (assoc ::x x ::y y ::z z))))

(defmethod ->cube ::grid
  [hexes]
  (map ->cube hexes))

(s/def ::grid (s/+ (s/alt :cubes ::cube :axials ::axial)))

(defmulti ->axial coordinate-system)

(defmethod ->axial ::axial
  [hex]
  hex)

(defmethod ->axial ::cube
  [{x ::x z ::z :as hex}]
  (-> hex
    (dissoc ::x ::y ::z)
    (assoc ::q x ::r z)))

(defn hex-reduce
  [f & hexes]
  (let [hexes (map ->cube hexes)]
    (zipmap cube-coords
      (map #(->> hexes
              (map %)
              (apply f))
        cube-coords))))

(defmulti zero coordinate-system)

(defmethod zero ::axial
  [_]
  (zipmap axial-coords (repeat 0)))

(defmethod zero ::cube
  [_]
  (zipmap cube-coords (repeat 0)))

(defmulti rotate
  "Rotate a hex around a point"
  (fn [hex & _]
    (coordinate-system hex)))

(defmethod rotate ::cube
  ([hex]
   (hex-reduce - (zipmap cube-coords
                   (->> hex
                     vals
                     cycle
                     (drop 1)
                     (take (count cube-coords))))))
  ([hex n-thirds-radians]
   (rotate hex n-thirds-radians (zero hex)))
  ([hex n-thirds-radians center]
   (-> (hex-reduce - hex center)
     (->> (iterate rotate))
     (nth n-thirds-radians)
     (->> (hex-reduce + center)))))


(defmethod rotate ::axial
  [hex & params]
  (-> hex
    ->cube
    (#(apply rotate % params))
    ->axial))

(defmethod rotate ::grid [grid & params]
  (map #(apply rotate % params) grid))

(defmulti translate
  (fn [hex & _]
    (coordinate-system hex)))

(defmethod translate ::axial [hex q-diff r-diff]
  (-> hex
    (update :q + q-diff)
    (update :r + r-diff)))

(defmethod translate ::cube [hex & params]
  (-> hex
    ->axial
    (#(apply translate % params))
    ->cube))

(defmethod translate ::grid [board & params]
  (map #(apply translate % params) board))
