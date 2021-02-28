(ns hex.coordinates-test
  (:require [hex.coordinates :as sut]
            [clojure.test :as t]
            [hex.cube :as cube]
            [hex.axial :as axial]))

(t/deftest conversion
  (let [cube-locs  [[-1 3 -2] [0 2 -2] [1 1 -2] [2 0 -2] [3 -1 -2]
                    [-2 3 -1] [-1 2 -1] [0 1 -1] [1 0 -1] [2 -1 -1]
                    [-2 2 0] [-1 1 0] [0 0 0] [1 -1 0] [2 -2 0]
                    [-3 2 1] [-2 1 1] [-1 0 1] [0 -1 1] [1 -2 1]
                    [-3 1 2] [-2 0 2] [-1 -1 2] [0 -2 2] [1 -3 2]]
        axial-locs [[-1 -2] [0 -2] [1 -2] [2 -2] [3 -2]
                    [-2 -1] [-1 -1] [0 -1] [1 -1] [2 -1]
                    [-2 0] [-1 0] [0 0] [1 0] [2 0]
                    [-3 1] [-2 1] [-1 1] [0 1] [1 1]
                    [-3 2] [-2 2] [-1 2] [0 2] [1 2]]]
    (t/testing "Axial to cube"
      (t/is (= cube-locs
              (->> axial-locs
                sut/->cube
                sut/->vectors))))
    (t/testing "Cube to axial"
      (t/is (= axial-locs
              (->> cube-locs
                sut/->axial
                sut/->vectors))))))
