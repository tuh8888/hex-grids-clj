(ns hex.core
  (:require [hex.axial :as axial]
            [hex.coordinates :as c]
            [hex.cube :as cube]))

(defn hex-reduce
  [f & hexes]
  (let [hexes (map c/->cube hexes)]
    (zipmap cube/coords
      (map #(->> hexes
              (map %)
              (apply f))
        cube/coords))))

(defmulti zero c/coordinate-system)

(defmethod zero ::c/axial
  [_]
  (zipmap axial/coords (repeat 0)))

(defmethod zero ::c/cube
  [_]
  (zipmap cube/coords (repeat 0)))

(defmulti rotate
  "Rotate a hex around a point"
  (fn [hex & _]
    (c/coordinate-system hex)))

(defmethod rotate ::c/cube
  ([hex]
   (hex-reduce - (zipmap cube/coords
                   (->> hex
                     vals
                     cycle
                     (drop 1)
                     (take (count cube/coords))))))
  ([hex n-thirds-radians]
   (rotate hex n-thirds-radians (zero hex)))
  ([hex n-thirds-radians center]
   (-> (hex-reduce - hex center)
     (->> (iterate rotate))
     (nth n-thirds-radians)
     (->> (hex-reduce + center)))))


(defmethod rotate ::c/axial
  [hex & params]
  (-> hex
    c/->cube
    (#(apply rotate % params))
    c/->axial))

(defmethod rotate ::c/grid [grid & params]
  (map #(apply rotate % params) grid))

(defmulti translate
  (fn [hex & _]
    (c/coordinate-system hex)))

(defmethod translate ::c/axial [hex q-diff r-diff]
  (-> hex
    (update :q + q-diff)
    (update :r + r-diff)))

(defmethod translate ::c/cube [hex & params]
  (-> hex
    c/->axial
    (#(apply translate % params))
    c/->cube))

(defmethod translate ::c/grid [board & params]
  (map #(apply translate % params) board))
