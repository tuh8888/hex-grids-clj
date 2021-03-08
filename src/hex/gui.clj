(ns hex.gui
  (:require [hex.axial :as axial]
            [quil.core :as q]))

(def base-vertices
  {::pointy (->> 6
              range
              (map #(->> %
                      (* q/THIRD-PI)
                      ((juxt q/sin q/cos)))))
   ;; TODO implement flat vertices
   ::flat   nil})

(defn vertices [{:keys [radius hex-type]}]
  (->> base-vertices
    hex-type
    (map (partial map (partial * radius)))))

(defn hexagon [point {:keys [hex-fn] :as state}]
  (q/begin-shape)
  (when hex-fn
    (hex-fn point state))
  (doseq [p (vertices state)]
    (apply q/vertex p))
  (q/end-shape :close))

(defn apothem [radius]
  (* radius (q/cos (/ q/PI 6))))

(defn ->cartesian
  [hex {:keys [radius hex-border-width]
        :or   {hex-border-width 0}}]
  (let [{r ::axial/r q ::axial/q} hex
        w                         (/ hex-border-width 2)]
    [(* (+ (* q 2) r) (+ w (apothem radius)))
     (* r 3/2 (+ radius (/ w (q/sin q/THIRD-PI))))]))

(defn honeycomb [{:keys [points] :as state}]
  (doseq [point points]
    (q/with-translation (->cartesian point state)
      (hexagon point state))))
