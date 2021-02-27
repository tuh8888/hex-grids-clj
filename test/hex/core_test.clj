(ns hex.core-test
  (:require [clojure.test :as t]
            [hex.core :as sut]))

(t/deftest rotation
  (t/testing "Arithmetic"
    (t/is (= #:hex.cube{:x 1 :y -1 :z 0}
            (sut/hex-reduce - #:hex.cube{:x -1 :y 1 :z 0})))
    (t/is (= #:hex.cube{:x 2 :y -2 :z 0}
            (sut/hex-reduce - #:hex.cube{:x 1 :y -1 :z 0}
              #:hex.cube{:x -1 :y 1 :z 0})))
    (t/is (= #:hex.cube{:x 1 :y 1 :z -2}
            (sut/hex-reduce + #:hex.cube{:x -1 :y 1 :z 0}
              #:hex.cube{:x 2 :y 0 :z -2}))))

  (t/testing "Single rotation"
    (t/is (= #:hex.cube{:x 1 :y 0 :z -1}
            (sut/rotate #:hex.cube{:x 1 :y -1 :z 0})))
    (t/is (= [#:hex.cube{:x 1 :y 0 :z -1}
              #:hex.cube{:x 0 :y 1 :z -1}
              #:hex.cube{:x -1 :y 1 :z 0}
              #:hex.cube{:x -1 :y 0 :z 1}
              #:hex.cube{:x 0 :y -1 :z 1}
              #:hex.cube{:x 1 :y -1 :z 0}]
            (-> [#:hex.cube{:x 1 :y -1 :z 0}
                 #:hex.cube{:x 1 :y 0 :z -1}
                 #:hex.cube{:x 0 :y 1 :z -1}
                 #:hex.cube{:x -1 :y 1 :z 0}
                 #:hex.cube{:x -1 :y 0 :z 1}
                 #:hex.cube{:x 0 :y -1 :z 1}]
              sut/rotate))))

  (t/testing "Multiple rotations"
    (t/is (= [#:hex.cube{:x 1 :y -1 :z 0}
              #:hex.cube{:x 1 :y 0 :z -1}
              #:hex.cube{:x 0 :y 1 :z -1}
              #:hex.cube{:x -1 :y 1 :z 0}
              #:hex.cube{:x -1 :y 0 :z 1}
              #:hex.cube{:x 0 :y -1 :z 1}
              #:hex.cube{:x 1 :y -1 :z 0}]
            (->> 7
              range
              (map (partial sut/rotate #:hex.cube{:x 1 :y -1 :z 0}))))))

  (t/testing "Rotations about a point"
    (t/is (= #:hex.cube{:x 1 :y 1 :z -2}
            (sut/rotate #:hex.cube{:x 1 :y -1 :z 0}
              1 #:hex.cube{:x -1 :y 1 :z 0})))))
