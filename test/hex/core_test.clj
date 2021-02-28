(ns hex.core-test
  (:require [clojure.test :refer [deftest is testing]]
            [hex.axial :as axial]
            [hex.coordinates :as c]
            [hex.core :as sut]
            [hex.cube :as cube]))

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

(deftest neighbors-test
  (testing "Center"
    (is (= {:up        [0 -1]
            :down      [0 1]
            :left      [-1 0]
            :right     [1 0]
            :up-right  [1 -1]
            :down-left [-1 1]}
          (->>  #:hex.axial{:q 0 :r 0}
            sut/neighbors
            (sut/map-vals c/->axial)
            (sut/map-vals vals)))))

  (testing "Off center"
    (is (= {:up        [1 -1]
            :down      [1 1]
            :left      [0 0]
            :right     [2 0]
            :up-right  [2 -1]
            :down-left [0 1]}
          (->>  #:hex.axial{:q 1 :r 0}
            sut/neighbors
            (sut/map-vals c/->axial)
            (sut/map-vals vals))))))

(deftest distance-test
  (testing "No change"
    (is (= 4
          (sut/distance #:hex.cube{:x 0 :y 0 :z 0}
            #:hex.cube{:x -2 :y -2 :z 4})))

    (is (= 4
          (sut/distance #:hex.cube{:x 1 :y -1 :z 0}
            #:hex.cube{:x -2 :y -2 :z 4}))))

  (testing "Difference"
    (is (= 5
          (sut/distance #:hex.cube{:x 2 :y -1 :z -1}
            #:hex.cube{:x -2 :y -2 :z 4})))))

(deftest round-test
  (testing "Center"
    (is (= [[0 0 0]]
          (c/->vectors [(sut/round #:hex.cube{:x 0.2 :y -0.3 :z 0.4})]))))
  (testing "Off center"
    (is (= [[1 0 -1]]
          (c/->vectors [(sut/round #:hex.cube{:x 0.8 :y -0.3 :z 0.4})])))
    (is (= #:hex.cube{:x -1 :y 1 :z 0}
          (sut/round #:hex.cube{:x -1.0 :y 1.0 :z 0.0})))))

(deftest line-to-test
  (testing "Straight line"
    (testing "From center"
      (is (= [[0 0] [1 0] [2 0] [3 0] [4 0]]
            (->> [[0 0] [4 0]]
              (apply sut/line-to)
              c/->axial
              c/->vectors)))
      (is (= [[0 0] [-1 0] [-2 0] [-3 0] [-4 0]]
            (->> [[0 0] [-4 0]]
              (apply sut/line-to)
              c/->axial
              c/->vectors)))
      (is (= [[0 0] [0 1] [0 2] [0 3] [0 4]]
            (->> [[0 0] [0 4]]
              (apply sut/line-to)
              c/->axial
              c/->vectors))))

    (testing "Non-center"
      (is (= [ [1 0] [2 0] [3 0] [4 0]]
            (->> [[1 0] [4 0]]
              (apply sut/line-to)
              c/->axial
              c/->vectors)))
      (is (= [ [-6 0] [-5 0] [-4 0]]
            (->> [[-6 0] [-4 0]]
              (apply sut/line-to)
              c/->axial
              c/->vectors)))
      (is (= [[0 -4] [0 -3] [0 -2] [0 -1] [0 0] [0 1] [0 2] [0 3] [0 4]]
            (->> [[0 -4] [0 4]]
              (apply sut/line-to)
              c/->axial
              c/->vectors)))))

  (testing "Bent line"
    (testing "From center"
      (is (= [[0 0] [1 0] [1 1]]
            (->> [[0 0] [1 1]]
              (apply sut/line-to)
              c/->axial
              c/->vectors)))
      (is (= [[0 0] [1 0] [2 0] [2 1] [3 1]]
            (->> [[0 0] [3 1]]
              (apply sut/line-to)
              c/->axial
              c/->vectors)))
      (is (= [[0 0] [1 0] [1 1] [2 1] [2 2]]
            (->> [[0 0] [2 2]]
              (apply sut/line-to)
              c/->axial
              c/->vectors))))))

(deftest hex-reachable?-test
  (testing "Hex reachable"
    (let [grid     (-> [#:hex.axial{:q 0 :r -2} #:hex.axial{:q 1 :r -2} #:hex.axial{:q 2 :r -2} #:hex.axial{:q 3 :r -2}
                        #:hex.axial{:q -1 :r -1} #:hex.axial{:q 0 :r -1} {::axial/q 1 ::axial/r -1 :blocked? true} {::axial/q 2 ::axial/r -1 :blocked? true} #:hex.axial{:q 3 :r -1}
                        #:hex.axial{:q -2 :r 0} #:hex.axial{:q -1 :r 0} #:hex.axial {:q 0 :r 0} #:hex.axial{:q 1 :r 0} {::axial/q 2 ::axial/r 0 :blocked? true} #:hex.axial{:q 3 :r 0}
                        {::axial/q -2 ::axial/r 1 :blocked? true} {::axial/q -1 ::axial/r 1 :blocked? true} #:hex.axial{:q 0 :r 1} #:hex.axial{:q 1 :r 1} {::axial/q 2 ::axial/r 1 :blocked? true}
                        {::axial/q -3 ::axial/r 2 :blocked? true} #:hex.axial{:q -2 :r 2} {::axial/q -1 ::axial/r 2 :blocked? true} {::axial/q 0 ::axial/r 2 :blocked? true} {::axial/q 1 ::axial/r 2 :blocked? true} #:hex.axial{:q 2 :r 2}
                        #:hex.axial{:q -3 :r 3} #:hex.axial{:q -2 :r 3} #:hex.axial{:q -1 :r 3} #:hex.axial{:q 0 :r 3} #:hex.axial{:q 1 :r 3}
                        #:hex.axial{:q -3 :r 4} #:hex.axial{:q -2 :r 4} #:hex.axial{:q -1 :r 4} #:hex.axial{:q 0 :r 4}]
                     c/->cube)
          grid-map (zipmap (map #(select-keys % cube/coords) grid)
                     grid)]
      (get-in grid-map [#:hex.cube{:x 1 :y 0 :z -1}])

      (is (= #{[0 0] [0 -1] [0 1] [1 0] [-1 0]}
            (-> #:hex.axial{:q 0 :r 0}
              (sut/reachable-hexes 1 (fn [hex] (not (get-in grid-map [hex :blocked?]))))
              c/->axial
              c/->vectors
              set))))))
