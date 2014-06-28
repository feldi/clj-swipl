(ns 
  ^{:author "Peter Feldtmann"
    :doc "Demo code for the Clojure SWI-Prolog bridge."}
  clj.swipl.demo
  (:require [clj.swipl :as pl]) 
  (:use clojure.pprint clojure.repl))


;;------------------------------------------------------------------------
;; demo functions
;;------------------------------------------------------------------------

(defn demo-family
  []
  (pl/consult "resources/family.pl")
  (let [var-x     (pl/make-var "X")
        atom-ralf (pl/make-atom "ralf")
        query     (pl/make-query-from-text-with-parms "descendent_of" [var-x atom-ralf])
        solutions (pl/get-all-solutions query)]
    (println "demo-family : all solutions to " (pl/show-query query)  " ==> " 
             (map pl/show-solution solutions))))

(defn demo-all-solutions
  []
  (let [query     (pl/make-query-from-text "X is 1; X is 2; X is 3")
        solutions (pl/get-all-solutions query)]
    (println "demo-all-solutions: all solutions to " (pl/show-query query)  " ==> " 
             (map pl/show-solution solutions))))

(defn demo-n-solutions
  []
  (let [solutions (pl/run-n-query-from-text 2 "X is 1; X is 2; X is 3")]
     (println "demo-n-solutions: only first 2 solutions to previous query ==> " 
             (map pl/show-solution solutions))))

(defn demo-append
  []
  (let [query    (pl/make-query-from-text "append([1, 2, 3], [4, 5], X).")
        solution (pl/get-one-solution query)]
    (println "demo-append / Prolog list processing. Query: " (pl/show-query query)
             " ==>" (pl/pl-list-to-clj-list (pl/get-value "X" solution )))))

(defn demo-compounds
  []
   (let [compound  (pl/make-compound "append" [(pl/make-var "Xs") 
                                               (pl/make-var "Ys") 
                                               (pl/seq-to-pl-list [(pl/make-atom "a") 
                                                                   (pl/make-atom "b") 
                                                                   (pl/make-atom "c")])])
         query     (pl/make-query-from-term compound)
         solutions (pl/get-all-solutions query)
         counter   (count solutions)]
    (println "demo-compounds: " (pl/show-query query) " has " counter "solutions :" )
    (doseq [x solutions] (println (pl/show-solution x)))))

(defn demo-lists
  []
  (let [pl-list (pl/seq-to-pl-list [(pl/make-atom "a") (pl/make-atom "b") (pl/make-atom "c")])
        clj-list (pl/pl-list-to-clj-list pl-list)
        clj-vec (pl/pl-list-to-vec pl-list)]
    (println "demo-with-lists: prolog-list = " pl-list 
             " ; as clj-list = " clj-list 
             " ; as clj-vec = " clj-vec))) 

(defn demo-with-params
  []
  (let [arg1 (pl/make-integer 11)
        arg2 (pl/make-integer 22)
        query-with-params (pl/make-query-from-text-with-parms "X = ?, Y = ? ", [arg1 arg2])
        solution (pl/get-one-solution query-with-params)]
    (println "demo-with-params: query with params: " (pl/show-query query-with-params) 
             " ==> " (pl/show-solution solution))))

(defn demo-get-value
  []
  (let [arg1 (pl/make-integer 11)
        arg2 (pl/make-integer 22)
        query-with-params (pl/make-query-from-text-with-parms "X = ?, Y = ? ", [arg1 arg2])
        solution (pl/get-one-solution query-with-params)]
    (println "demo-get-value: query with params: " (pl/show-query query-with-params) 
             " ==> X resolved to " (pl/get-value "X" solution) 
             ", Y resolved to " (pl/get-value "Y" solution))))

(defn demo-lib-version
  []
   (let [query    (pl/make-query-from-text "jpl_pl_lib_version(Version)")
        solution (pl/get-one-solution query)]
    (println "demo-lib-version : JPL version check: prolog jpl library version = "  (pl/get-value "Version" solution )
             "; Java jpl library version = " (pl/get-version-as-string))))

(defn demo-prolog-exception
  []
  (try 
    (pl/make-query-from-text "p(].") 
    (catch jpl.PrologException exc 
      (println "demo-prolog-exception: goal 'p(].' ==> "
               (pl/get-term-from-exception exc)))))

(defn demo-stats
  []
  (let [query (pl/make-query-from-text "statistics")]
    (println "demo-stats : Getting SWI-Prolog statistics. Check console output.")
    (pl/get-one-solution query)))

;;------------------------------------------------------------------------
;; run the demos
;;------------------------------------------------------------------------

(demo-family)
(demo-all-solutions)
(demo-n-solutions) 
(demo-append)
(demo-compounds)
(demo-lists)
(demo-with-params)
(demo-get-value)
(demo-lib-version)
(demo-prolog-exception)
(demo-stats)


;; EOF