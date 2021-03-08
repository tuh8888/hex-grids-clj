(ns hex.gui
  (:require [hex.coordinates :as c]
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

(defn hexagon [{:keys [hex-fn] :as state}]
  (q/begin-shape)
  (when hex-fn
    (hex-fn state))
  (doseq [p (vertices state)]
    (apply q/vertex p))
  (q/end-shape :close))

(defn honeycomb [{:keys [radius points] :as state}]
  (doseq [point points]
    (q/with-translation (c/->cartesian radius point)
      (hexagon state))))
