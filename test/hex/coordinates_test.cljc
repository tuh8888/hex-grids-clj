(ns hex.coordinates-test
  (:require #?(:clj [clojure.test :refer [deftest testing is]]
               :cljs [cljs.test :refer [deftest testing is] :include-macros
                      true])
            [hex.coordinates :as sut]))

(deftest conversion
  (let [cube-locs  [[-1 3 -2] [0 2 -2] [1 1 -2] [2 0 -2] [3 -1 -2] [-2 3 -1]
                    [-1 2 -1] [0 1 -1] [1 0 -1] [2 -1 -1] [-2 2 0] [-1 1 0]
                    [0 0 0] [1 -1 0] [2 -2 0] [-3 2 1] [-2 1 1] [-1 0 1]
                    [0 -1 1] [1 -2 1] [-3 1 2] [-2 0 2] [-1 -1 2] [0 -2 2]
                    [1 -3 2]]
        axial-locs [[-1 -2] [0 -2] [1 -2] [2 -2] [3 -2] [-2 -1] [-1 -1] [0 -1]
                    [1 -1] [2 -1] [-2 0] [-1 0] [0 0] [1 0] [2 0] [-3 1] [-2 1]
                    [-1 1] [0 1] [1 1] [-3 2] [-2 2] [-1 2] [0 2] [1 2]]]
    (testing "Axial to cube"
     (is (= cube-locs
            (->> axial-locs
                 sut/->cube
                 sut/->vectors))))
    (testing "Cube to axial"
     (is (= axial-locs
            (->> cube-locs
                 sut/->axial
                 sut/->vectors))))))
