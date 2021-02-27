(ns coordinates-test
  (:require [coordinates :as sut]
            [clojure.test :as t]))

(t/deftest conversion
  (let [cube-locs  [#:cube{:x -1 :y 3 :z -2} #:cube{:x 0 :y 2 :z -2} #:cube{:x 1 :y 1 :z -2} #:cube{:x 2 :y 0 :z -2} #:cube{:x 3 :y -1 :z -2}
                    #:cube{:x -2 :y 3 :z -1} #:cube{:x -1 :y 2 :z -1} #:cube{:x 0 :y 1 :z -1} #:cube{:x 1 :y 0 :z -1} #:cube{:x 2 :y -1 :z -1}
                    #:cube{:x -2 :y 2 :z 0} #:cube{:x -1 :y 1 :z 0} #:cube{:x 0 :y 0 :z 0} #:cube{:x 1 :y -1 :z 0} #:cube{:x 2 :y -2 :z 0}
                    #:cube{:x -3 :y 2 :z 1} #:cube{:x -2 :y 1 :z 1} #:cube{:x -1 :y 0 :z 1} #:cube{:x 0 :y -1 :z 1} #:cube{:x 1 :y -2 :z 1}
                    #:cube{:x -3 :y 1 :z 2} #:cube{:x -2 :y 0 :z 2} #:cube{:x -1 :y -1 :z 2} #:cube{:x 0 :y -2 :z 2} #:cube{:x 1 :y -3 :z 2}]
        axial-locs [#:axial{:q -1 :r -2} #:axial{:q 0 :r -2} #:axial{:q 1 :r -2} #:axial{:q 2 :r -2} #:axial{:q 3 :r -2}
                    #:axial{:q -2 :r -1} #:axial{:q -1 :r -1} #:axial{:q 0 :r -1} #:axial{:q 1 :r -1} #:axial{:q 2 :r -1}
                    #:axial{:q -2 :r 0} #:axial{:q -1 :r 0} #:axial{:q 0 :r 0} #:axial{:q 1 :r 0} #:axial{:q 2 :r 0}
                    #:axial{:q -3 :r 1} #:axial{:q -2 :r 1} #:axial{:q -1 :r 1} #:axial{:q 0 :r 1} #:axial{:q 1 :r 1}
                    #:axial{:q -3 :r 2} #:axial{:q -2 :r 2} #:axial{:q -1 :r 2} #:axial{:q 0 :r 2} #:axial{:q 1 :r 2}]]
    (t/testing "Axial to cube"
      (t/is (= cube-locs (map #'sut/->cube axial-locs))))
    (t/testing "Cube to axial"
      (t/is (= axial-locs (map #'sut/->axial cube-locs))))))
