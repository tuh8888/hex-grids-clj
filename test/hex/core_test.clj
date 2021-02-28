(ns hex.core-test
  (:require [clojure.test :as t]
            [hex.core :as sut]
            [hex.coordinates :as c]))

(t/deftest hex-reduce-test
  (t/testing "Arithmetic"
    (t/testing "Subtraction"
      (t/is (= #:hex.cube{:x 1 :y -1 :z 0}
              (sut/hex-reduce - #:hex.cube{:x -1 :y 1 :z 0})))
      (t/is (= #:hex.cube{:x 2 :y -2 :z 0}
              (sut/hex-reduce - #:hex.cube{:x 1 :y -1 :z 0}
                #:hex.cube{:x -1 :y 1 :z 0}))))

    (t/testing "Addition"
      (t/is (= #:hex.cube{:x 1 :y 1 :z -2}
              (sut/hex-reduce + #:hex.cube{:x -1 :y 1 :z 0}
                #:hex.cube{:x 2 :y 0 :z -2}))))))

(t/deftest rotate-test
  (t/testing "Single rotation"
    (t/is (= #:hex.cube{:x 1 :y 0 :z -1}
            (sut/rotate #:hex.cube{:x 1 :y -1 :z 0})))
    (t/is (= [[1 0 -1] [0 1 -1]
              [-1 1 0] [-1 0 1]
              [0 -1 1] [1 -1 0]]
            (-> [[1 -1 0] [1 0 -1]
                 [0 1 -1] [-1 1 0]
                 [-1 0 1] [0 -1 1]]
              c/->cube
              sut/rotate
              c/->vectors))))

  (t/testing "Multiple rotations"
    (t/is (= [[1 -1 0] [1 0 -1]
              [0 1 -1] [-1 1 0]
              [-1 0 1] [0 -1 1]
              [1 -1 0]]
            (->> 7
              range
              (map (partial sut/rotate #:hex.cube{:x 1 :y -1 :z 0}))
              c/->vectors))))

  (t/testing "Rotations about a point"
    (t/is (= #:hex.cube{:x 1 :y 1 :z -2}
            (sut/rotate #:hex.cube{:x 1 :y -1 :z 0}
              1 #:hex.cube{:x -1 :y 1 :z 0})))))

(t/deftest translate-test)
