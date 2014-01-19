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
  (let [var-x (pl/make-variable "X")
        atom-ralf (pl/make-atom "ralf")
        query (pl/make-query-from-terms "descendent_of" [var-x atom-ralf])
        solutions (pl/all-solutions query)]
    (println "q1: Solutions to " (pl/show-query query)  " ==> " 
             (map #(str "X = " (pl/get-value var-x %) "; ") solutions))))

(defn q2
  []
  (let [query (pl/make-query-from-source "X is 1; X is 2; X is 3")
        solutions (pl/all-solutions query)]
    (println "q2: Solutions to " (pl/show-query query)  " ==> " 
             (map #(str "X = " (pl/get-value "X" %) "; ") solutions))))

(defn q3
  []
  (let [arg1 (pl/make-integer 11)
        arg2 (pl/make-integer 22)
        query-with-params (pl/make-query-with-parms "X = ?, Y = ? ", [arg1 arg2])
        solution (pl/one-solution query-with-params)]
    (println "q3: Query with params: " (pl/show-query query-with-params) 
             " ==> X = " (pl/get-value "X" solution) 
             ", Y = " (pl/get-value "Y" solution))))

(defn q4
  []
  (let [query (pl/make-query-from-source "append([1, 2, 3], [4, 5], X).")
        solution (pl/one-solution query)]
    (println "q4: Prolog list processing. Query: " (pl/show-query query)
             " ==>" (pl/pl-list-to-clj-list (pl/get-value "X" solution ))) 
    ))

(defn q5
  []
  (let [query (pl/make-query-from-source "statistics")]
    (println "q5: Output SWI-Prolog statistics.")
    (pl/one-solution query)))

;;------------------------------------------------------------------------
;; run the demos
;;------------------------------------------------------------------------

(q1)
(q2)
(q3)
(q4)
(q5)
