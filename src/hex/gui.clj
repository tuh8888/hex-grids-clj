(ns hex.gui
  (:require [hex.coordinates :as c]
            [hex.core :as hex]
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

(defn hexagon [state]
  (doseq [[p1 p2] (->> state
                    vertices
                    ((juxt identity (comp rest cycle)))
                    (apply map vector))]
    (q/line p1 p2)))

(defn honeycomb [{:keys [radius points] :as state}]
  (doseq [point points]
    (q/with-translation (c/->cartesian radius point)
      (hexagon state))))
