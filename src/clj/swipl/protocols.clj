(ns 
  ^{:author "Peter Feldtmann"
    :doc "Protocols for the Clojure SWI-Prolog bridge."}
clj.swipl.protocols
  
  )

(defprotocol ICljToPrologConversion
  "Conversion of Clojure data to Prolog data."
  (to-pl [data])
  )

(defprotocol IPrologToCljConversion
   "Conversion of Prolog data to Clojure data."
    (to-clj [data])
  )

(defprotocol IPrologQuery
   "Handling of Prolog queries."
   (build-q [textOrTerm]
            "build a new query from prolog source text or a term (goal).")
   (build-q-with-param [text param]
                       "build a new query from source text with a single parameter.")
   (build-q-with-params [text params]
                        "build a new query from source text with '?'-parameter substitutions.")
   (has-q-solution? [textOrTerm]
                     "Returns true if the goal is satisfiable." )
   (has-q-solution-with-params? [text params]
                                "Returns true if the goal, build with '?'-parameter substitutions, is satisfiable.")
   (run-q-1 [textOrTerm]
            "Run query with term goal. Returns only first solution, if any.")
   (run-q-1-with-params [text params]
                        "Query with term goal, build with '?'-parameter substitutions. Returns only first solution, if any.")
   (run-q [textOrTerm]
          "Run query with term goal. Returns all solutions.")
   (run-q-with-params [text params]
                      "Run query with term goal, build with '?'-parameter substitutions. Returns all solutions.")
   (run-q-n [textOrTerm n]
            "Run query with term goal. Returns the given number of solutions.")
   (run-q-n-with-params [text params n]
                        "Run query with term goal, build with '?'-parameter substitutions. Returns the given number of solutions.")
  )

(defprotocol IPrologSolution
   "Handling of Prolog solution maps (Hashtables)."
   (get-value [soln var])
  )

(defprotocol IPrologSourceText
  "Handling of Prolog source text."
  (text-to-pl [text])
  (pl-to-text [term])
  (pl-to-inspection-text [term])
  )
