(ns core-test
  (:require [clojure.test :as t]
            [core :as sut]
            [clojure.spec.alpha :as s]))


(t/deftest conversion
  (let [cube-coords  [#:core{:x -1 :y 3 :z -2} #:core{:x 0 :y 2 :z -2} #:core{:x 1 :y 1 :z -2} #:core{:x 2 :y 0 :z -2} #:core{:x 3 :y -1 :z -2}
                      #:core{:x -2 :y 3 :z -1} #:core{:x -1 :y 2 :z -1} #:core{:x 0 :y 1 :z -1} #:core{:x 1 :y 0 :z -1} #:core{:x 2 :y -1 :z -1}
                      #:core{:x -2 :y 2 :z 0} #:core{:x -1 :y 1 :z 0} #:core{:x 0 :y 0 :z 0} #:core{:x 1 :y -1 :z 0} #:core{:x 2 :y -2 :z 0}
                      #:core{:x -3 :y 2 :z 1} #:core{:x -2 :y 1 :z 1} #:core{:x -1 :y 0 :z 1} #:core{:x 0 :y -1 :z 1} #:core{:x 1 :y -2 :z 1}
                      #:core{:x -3 :y 1 :z 2} #:core{:x -2 :y 0 :z 2} #:core{:x -1 :y -1 :z 2} #:core{:x 0 :y -2 :z 2} #:core{:x 1 :y -3 :z 2}]
        axial-coords [#:core{:q -1 :r -2} #:core{:q 0 :r -2} #:core{:q 1 :r -2} #:core{:q 2 :r -2} #:core{:q 3 :r -2}
                      #:core{:q -2 :r -1} #:core{:q -1 :r -1} #:core{:q 0 :r -1} #:core{:q 1 :r -1} #:core{:q 2 :r -1}
                      #:core{:q -2 :r 0} #:core{:q -1 :r 0} #:core{:q 0 :r 0} #:core{:q 1 :r 0} #:core{:q 2 :r 0}
                      #:core{:q -3 :r 1} #:core{:q -2 :r 1} #:core{:q -1 :r 1} #:core{:q 0 :r 1} #:core{:q 1 :r 1}
                      #:core{:q -3 :r 2} #:core{:q -2 :r 2} #:core{:q -1 :r 2} #:core{:q 0 :r 2} #:core{:q 1 :r 2}]]
    (t/testing "Axial to cube"
      (t/is (= cube-coords (map #'sut/->cube axial-coords))))
    (t/testing "Cube to axial"
      (t/is (= axial-coords (map #'sut/->axial cube-coords))))))

(t/deftest rotation
  (t/testing "Arithmetic"
    (t/is (= #:core{:x 1 :y -1 :z 0}
            (sut/hex-reduce - #:core{:x -1 :y 1 :z 0})))
    (t/is (= #:core{:x 2 :y -2 :z 0}
            (sut/hex-reduce - #:core{:x 1 :y -1 :z 0}
              #:core{:x -1 :y 1 :z 0})))
    (t/is (= #:core{:x 1 :y 1 :z -2}
            (sut/hex-reduce + #:core{:x -1 :y 1 :z 0}
              #:core{:x 2 :y 0 :z -2}))))

  (t/testing "Single rotation"
    (t/is (= #:core{:x 1 :y 0 :z -1}
            (sut/rotate #:core{:x 1 :y -1 :z 0})))
    (t/is (= [#:core{:x 1 :y 0 :z -1}
              #:core{:x 0 :y 1 :z -1}
              #:core{:x -1 :y 1 :z 0}
              #:core{:x -1 :y 0 :z 1}
              #:core{:x 0 :y -1 :z 1}
              #:core{:x 1 :y -1 :z 0}]
            (-> [#:core{:x 1 :y -1 :z 0}
                 #:core{:x 1 :y 0 :z -1}
                 #:core{:x 0 :y 1 :z -1}
                 #:core{:x -1 :y 1 :z 0}
                 #:core{:x -1 :y 0 :z 1}
                 #:core{:x 0 :y -1 :z 1}]
              sut/rotate))))

  (t/testing "Multiple rotations"
    (t/is (= [#:core{:x 1 :y -1 :z 0}
              #:core{:x 1 :y 0 :z -1}
              #:core{:x 0 :y 1 :z -1}
              #:core{:x -1 :y 1 :z 0}
              #:core{:x -1 :y 0 :z 1}
              #:core{:x 0 :y -1 :z 1}
              #:core{:x 1 :y -1 :z 0}]
            (->> 7
              range
              (map (partial sut/rotate #:core{:x 1 :y -1 :z 0}))))))

  (t/testing "Rotations about a point"
    (t/is (= #:core{:x 1 :y 1 :z -2}
            (sut/rotate #:core{:x 1 :y -1 :z 0}
              1 #:core{:x -1 :y 1 :z 0})))))

(sut/hex-reduce + (nth (iterate sut/rotate  #:core{:x 1 :y -1 :z 0} ) 2) #:core{:x 1 :y 0 :z -1})
