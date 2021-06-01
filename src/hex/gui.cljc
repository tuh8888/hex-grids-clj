(ns hex.gui
  (:require [hex.axial :as axial]))

(def base-vertices
  {::pointy (->> 6
                 range
                 (map (fn [i]
                        (let [c (* (/ Math/PI 3) i)
                              x (Math/sin c)
                              y (Math/cos c)]
                          [x y]))))
   ;; TODO implement flat vertices
   ::flat   nil})

(defn map-vals
  [f m]
  (->> m
       (map (juxt key (comp f val)))
       (into {})))

(defn vertices
  [{:keys [radius orientation]}]
  (->> base-vertices
       orientation
       (map (partial map (partial * radius)))))

(defn apothem [radius] (* radius (Math/cos (/ Math/PI 6))))

(defn ->cartesian
  [{r ::axial/r
    q ::axial/q}
   {:keys [radius border-width]
    :or   {border-width 0}}]
  (let [w (/ border-width 2)
        x (* (+ (* q 2) r) (+ w (apothem radius)))
        y (* r
             #?(:clj 3/2
                :cljs 1.5)
             (+ radius (/ w (Math/sin (/ Math/PI 3)))))]
    [x y]))

(defn translate [base translation] (map + base translation))

(defn hexagon
  [hex params]
  (let [hex (->cartesian hex params)]
    (->> params
         vertices
         (map (partial translate hex)))))
