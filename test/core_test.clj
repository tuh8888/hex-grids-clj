(ns core-test
  (:require [clojure.test :as t]
            [core :as sut]))

(t/deftest rotation
  (t/testing "Arithmetic"
    (t/is (= #:cube{:x 1 :y -1 :z 0}
            (sut/hex-reduce - #:cube{:x -1 :y 1 :z 0})))
    (t/is (= #:cube{:x 2 :y -2 :z 0}
            (sut/hex-reduce - #:cube{:x 1 :y -1 :z 0}
              #:cube{:x -1 :y 1 :z 0})))
    (t/is (= #:cube{:x 1 :y 1 :z -2}
            (sut/hex-reduce + #:cube{:x -1 :y 1 :z 0}
              #:cube{:x 2 :y 0 :z -2}))))

  (t/testing "Single rotation"
    (t/is (= #:cube{:x 1 :y 0 :z -1}
            (sut/rotate #:cube{:x 1 :y -1 :z 0})))
    (t/is (= [#:cube{:x 1 :y 0 :z -1}
              #:cube{:x 0 :y 1 :z -1}
              #:cube{:x -1 :y 1 :z 0}
              #:cube{:x -1 :y 0 :z 1}
              #:cube{:x 0 :y -1 :z 1}
              #:cube{:x 1 :y -1 :z 0}]
            (-> [#:cube{:x 1 :y -1 :z 0}
                 #:cube{:x 1 :y 0 :z -1}
                 #:cube{:x 0 :y 1 :z -1}
                 #:cube{:x -1 :y 1 :z 0}
                 #:cube{:x -1 :y 0 :z 1}
                 #:cube{:x 0 :y -1 :z 1}]
              sut/rotate))))

  (t/testing "Multiple rotations"
    (t/is (= [#:cube{:x 1 :y -1 :z 0}
              #:cube{:x 1 :y 0 :z -1}
              #:cube{:x 0 :y 1 :z -1}
              #:cube{:x -1 :y 1 :z 0}
              #:cube{:x -1 :y 0 :z 1}
              #:cube{:x 0 :y -1 :z 1}
              #:cube{:x 1 :y -1 :z 0}]
            (->> 7
              range
              (map (partial sut/rotate #:cube{:x 1 :y -1 :z 0}))))))

  (t/testing "Rotations about a point"
    (t/is (= #:cube{:x 1 :y 1 :z -2}
            (sut/rotate #:cube{:x 1 :y -1 :z 0}
              1 #:cube{:x -1 :y 1 :z 0})))))
