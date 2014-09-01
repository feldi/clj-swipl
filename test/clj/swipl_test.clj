(ns clj.swipl-test
  (:require [clojure.test :refer :all]
            [clj.swipl.protocols :refer :all]
            [clj.swipl.core :refer :all]))

(deftest jpl-tests
  
  (testing "version"
           (is (= (get-version-as-string) "3.1.4-alpha")))
  
  (testing "prolog lists"
           (let [pl-list (to-pl [(to-pl "a") (to-pl "b") (to-pl "c")])
                 clj-list (pl-list-to-clj-list pl-list)
                 clj-vec (pl-list-to-vec pl-list)]
             (is (= '("a" "b" "c") clj-list))
             (is (= ["a" "b" "c"] clj-vec))))
  
  (testing "prolog exception"
           (is (thrown? jpl.PrologException (make-query-from-text "p(]."))))
  
  )
