(ns 
  ^{:author "Peter Feldtmann"
    :doc "Demo code for the Clojure SWI-Prolog bridge."}
  clj.swipl.demo
  (:require [clj.swipl :as pl]) 
  (:use clojure.pprint clojure.repl))


;;------------------------------------------------------------------------
;; demo functions
;;------------------------------------------------------------------------

(defn q1
  []
  (pl/consult "resources/family.pl")
  (let [var-x     (pl/make-var "X")
        atom-ralf (pl/make-atom "ralf")
        query     (pl/make-query-from-terms "descendent_of" [var-x atom-ralf])
        solutions (pl/get-all-solutions query)]
    (println "q1: solutions to " (pl/show-query query)  " ==> " 
             (map pl/show-solution solutions))))

(defn q2
  []
  (let [query     (pl/make-query-from-source "X is 1; X is 2; X is 3")
        solutions (pl/get-all-solutions query)]
    (println "q2: solutions to " (pl/show-query query)  " ==> " 
             (map pl/show-solution solutions))))

(defn q2b
  []
  (let [solutions (pl/run-n-query-from-source 2 "X is 1; X is 2; X is 3")]
     (println "q2b: only first 2 solutions to previous query ==> " 
             (map pl/show-solution solutions))))

(defn q3
  []
  (let [arg1 (pl/make-integer 11)
        arg2 (pl/make-integer 22)
        query-with-params (pl/make-query-with-parms "X = ?, Y = ? ", [arg1 arg2])
        solution (pl/get-one-solution query-with-params)]
    (println "q3: query with params: " (pl/show-query query-with-params) 
             " ==> " (pl/show-solution solution))))

(defn q3a
  []
  (let [arg1 (pl/make-integer 11)
        arg2 (pl/make-integer 22)
        query-with-params (pl/make-query-with-parms "X = ?, Y = ? ", [arg1 arg2])
        solution (pl/get-one-solution query-with-params)]
    (println "q3a: query with params: " (pl/show-query query-with-params) 
             " ==> X resolved to " (pl/get-value "X" solution) 
             ", Y resolved to " (pl/get-value "Y" solution))))

(defn q4
  []
  (let [query    (pl/make-query-from-source "append([1, 2, 3], [4, 5], X).")
        solution (pl/get-one-solution query)]
    (println "q4: Prolog list processing. Query: " (pl/show-query query)
             " ==>" (pl/pl-list-to-clj-list (pl/get-value "X" solution ))) 
    ))

(defn q5
  []
  (let [query (pl/make-query-from-source "statistics")]
    (println "q5: Getting SWI-Prolog statistics. Check console output.")
    (pl/get-one-solution query)))

;;------------------------------------------------------------------------
;; run the demos
;;------------------------------------------------------------------------

(q1)
(q2)
(q2b) 
(q3)
(q3a)
(q4)
(q5)
