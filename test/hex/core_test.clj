(ns hex.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [hex.coordinates :as c]
            [hex.core :as sut]))

(deftest hex-reduce-test
  (testing "Arithmetic"
    (testing "Subtraction"
      (is (= #:hex.cube{:x 1 :y -1 :z 0}
              (sut/hex-reduce - #:hex.cube{:x -1 :y 1 :z 0})))
      (is (= #:hex.cube{:x 2 :y -2 :z 0}
              (sut/hex-reduce - #:hex.cube{:x 1 :y -1 :z 0}
                #:hex.cube{:x -1 :y 1 :z 0}))))

    (testing "Addition"
      (is (= #:hex.cube{:x 1 :y 1 :z -2}
              (sut/hex-reduce + #:hex.cube{:x -1 :y 1 :z 0}
                #:hex.cube{:x 2 :y 0 :z -2}))))))

(deftest rotate-test
  (testing "Single rotation"
    (is (= #:hex.cube{:x 1 :y 0 :z -1}
            (sut/rotate #:hex.cube{:x 1 :y -1 :z 0})))
    (is (= [[1 0 -1] [0 1 -1]
              [-1 1 0] [-1 0 1]
              [0 -1 1] [1 -1 0]]
            (-> [[1 -1 0] [1 0 -1]
                 [0 1 -1] [-1 1 0]
                 [-1 0 1] [0 -1 1]]
              c/->cube
              sut/rotate
              c/->vectors))))

  (testing "Multiple rotations"
    (is (= [[1 -1 0] [1 0 -1]
              [0 1 -1] [-1 1 0]
              [-1 0 1] [0 -1 1]
              [1 -1 0]]
            (->> 7
              range
              (map (partial sut/rotate #:hex.cube{:x 1 :y -1 :z 0}))
              c/->vectors))))

  (testing "Rotations about a point"
    (is (= #:hex.cube{:x 1 :y 1 :z -2}
            (sut/rotate #:hex.cube{:x 1 :y -1 :z 0}
              1 #:hex.cube{:x -1 :y 1 :z 0})))))

(deftest translate-test)
