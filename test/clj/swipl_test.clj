(ns clj.swipl-test
  (:require [clojure.test :refer :all]
            [clj.swipl :refer :all]))

(deftest jpl-tests
  
  (testing "version"
           (is (= (get-version-as-string) "3.1.4-alpha")))
  
  (testing "prolog lists"
           (let [pl-list (seq-to-pl-list [(make-atom "a") (make-atom "b") (make-atom "c")])
                 clj-list (pl-list-to-clj-list pl-list)
                 clj-vec (pl-list-to-vec pl-list)]
             (is (= '("a" "b" "c") clj-list))
             (is (= ["a" "b" "c"] clj-vec))))
  
  (testing "prolog exception"
           (is (thrown? jpl.PrologException (make-query-from-text "p(].")))
           )
  )
