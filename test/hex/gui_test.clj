(ns hex.gui-test
  (:require [clojure.test :refer [deftest is testing]]
            [hex.gui :as sut]
            [quil.core :as q]
            [quil.middleware :as m]
            [hex.axial :as axial]))

(deftest hex-grid-test
  (testing "Draw a default grid of hexes"
    (declare hex-grid)
    (is (= #'hex-grid
          (q/defsketch hex-grid
            :size [300 300]
            :setup (fn []
                     (q/background 200)
                     {:radius   30
                      :hex-fn   (fn [_]
                                  (q/stroke 255 0 0))
                      :hex-type ::sut/pointy
                      :points   (for [q (range 0 4)
                                      r (range 0 4)]
                                  (zipmap axial/coords [q r]))})
            :draw (fn [state]
                    (q/with-translation [(/ (q/width) 4) (/ (q/height) 4)]
                      (sut/honeycomb state)))
            :middleware [m/fun-mode])))))
