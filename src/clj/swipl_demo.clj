(ns 
  ^{:author "Peter Feldtmann"
    :doc "Demo code for the Clojure SWI-Prolog bridge."}
  clj.swipl.demo
  (:require [clj.swipl.core :as pl]
            [clj.swipl.protocols :as p]) 
  (:use clojure.pprint clojure.repl))


;;------------------------------------------------------------------------
;; demo functions
;;------------------------------------------------------------------------

(defn demo-family
  []
  (pl/consult "resources/family.pl")
  (let [var-x     (p/to-pl "X")
        atom-ralf (p/to-pl "ralf")
        query     (p/build-q-with-params "descendent_of" [var-x atom-ralf])
        solutions (p/run-q query)]
    (println "demo-family : all solutions to " (p/pl-to-text query)  " ==> " 
             (map p/pl-to-text solutions))))

(defn demo-all-solutions
  []
  (let [query     (p/build-q "X is 1; X is 2; X is 3")
        solutions (p/run-q query)]
    (println "demo-all-solutions: all solutions to " (p/pl-to-text query)  " ==> " 
             (map p/pl-to-text solutions))))

(defn demo-n-solutions
  []
  (let [solutions (p/run-q-n "X is 1; X is 2; X is 3" 2 )]
     (println "demo-n-solutions: only first 2 solutions to previous query ==> " 
             (map p/pl-to-text solutions))))

(defn demo-append
  []
  (let [query    (p/build-q "append([1, 2, 3], [4, 5], X).")
        solution (p/run-q-1 query)]
    (println "demo-append / Prolog list processing. Query: " (p/pl-to-text query)
             " ==>" (p/get-value solution "X"))))

(defn demo-compounds
  []
   (let [compound  (pl/compound "append" [(p/to-pl "Xs") 
                                          (p/to-pl "Ys") 
                                          (p/to-pl [(p/to-pl "a") 
                                                    (p/to-pl "b") 
                                                    (p/to-pl "c")])])
         query     (p/build-q compound)
         solutions (p/run-q query)
         counter   (count solutions)]
    (println "demo-compounds: " (p/pl-to-text query) " has " counter "solutions :" )
    (doseq [x solutions] (pl/show-solution x) (println))))

(defn demo-lists
  []
  (let [pl-list (p/to-pl [(p/to-pl "a") (p/to-pl "b") (p/to-pl "c")])
        clj-list (pl/pl-list-to-clj-list pl-list)
        clj-vec (pl/pl-list-to-vec pl-list)]
    (println "demo-lists: prolog-list = " (p/pl-to-text pl-list) 
             " ; as clj-list = " clj-list 
             " ; as clj-vec = " clj-vec))) 

(defn demo-with-params
  []
  (let [arg1 (p/to-pl 11)
        arg2 (p/to-pl 22)
        query-with-params (p/build-q-with-params "X = ?, Y = ? ", [arg1 arg2])
        solution (p/run-q-1 query-with-params)]
    (println "demo-with-params: query with params: " (p/pl-to-text query-with-params) 
             " ==> " (p/pl-to-text solution))))

(defn demo-get-value
  []
  (let [arg1 (p/to-pl 11)
        arg2 (p/to-pl 22)
        query-with-params (p/build-q-with-params "X = ?, Y = ? ", [arg1 arg2])
        solution (p/run-q-1 query-with-params)]
    (println "demo-get-value: query with params: " (p/pl-to-text query-with-params) 
             " ==> X resolved to " (p/get-value solution "X") 
             ", Y resolved to " (p/get-value solution "Y"))))

(defn demo-lib-version
  []
   (let [query    (p/build-q "jpl_pl_lib_version(Version)")
        solution (p/run-q-1 query)]
    (println "demo-lib-version : JPL version check: prolog jpl library version = "  (p/get-value solution "Version"  )
             "; Java jpl library version = " (pl/get-version-as-string))))

(defn demo-prolog-exception
  []
  (try 
    (p/build-q "p(].") 
    (catch jpl.JPLException exc 
      (println "demo-prolog-exception: goal 'p(].' ==> "
               (pl/get-term-from-exception exc)))))

(defn demo-stats
  []
  (let [query (p/build-q "statistics")]
    (println "demo-stats : Getting SWI-Prolog statistics. Check console output.")
    (p/run-q-1 query)))

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