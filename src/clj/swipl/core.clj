(ns 
  ^{:author "Peter Feldtmann"
    :doc "A Clojure SWI-Prolog bridge.
          Call prolog goals directly from clojure code."}
  clj.swipl.core
  (:use [clj.swipl.protocols])
  (:import [jpl JPL Atom Compound JPLException
            PrologException Query Term
            Util Variable JRef])
  )

; to install the SWI-Prolog java bridge jpl.jar:
; get and install the leiningen plugin 'localrepo',
; then do: lein localrepo install 'path-to-swi-prolog'/lib/jpl.jar jpl 3.1.4-alpha

(set! *warn-on-reflection* true)


;; JPL main class

(defn get-version-as-string
  "Get jpl version." 
  []
  (JPL/version_string))

(defn init
  "Force explicit initialization using the default init parameters."
  []
  (JPL/init))

(defn init-with-args
  "Force explicit initialization with parameters other than the defaults,
   provided as a vector of strings."
  [args-list]
  (JPL/init (into-array String args-list)))

(defn get-default-init-args
  []
  (into [] (JPL/getDefaultInitArgs)))

(defn set-default-init-args!
  "Set init parameters to be used when the prolog engine is not
   explicitly initialized, i.e. automatically at the first query open.
   Provide the parameters as a vector of strings." 
  [args-list]
  (JPL/setDefaultInitArgs (into-array String args-list)))

(defn get-actual-init-args
  []
  (into [] (JPL/getActualInitArgs)))

(defn set-dtm-mode!
  "Set or unset 'dont-tell-me' mode."
  [^Boolean mode]
  (JPL/setDTMMode mode))

(defn tag? 
  [^String s]
  (JPL/isTag s))


;; Util

(defn ^Term text-to-term
  [^String text]
  (Util/textToTerm text))

(defn ^Term text-with-params-to-term
  [^String text args]
  (Util/textParamsToTerm text (into-array Term  args)))

(defn ^Term seq-to-pl-list
  [terms]
  (Util/termArrayToList (into-array Term terms)))

(defn ^Term string-list-to-pl-list
  [list-of-strings]
  (Util/stringArrayToList (into-array String list-of-strings)))

(defn ^Term int-list-to-pl-list
  [list-of-ints]
  (Util/intArrayToList (int-array list-of-ints)))

(defn pl-list-to-length
  [term]
  (Util/listToLength term))

(defn pl-list-to-vec
  [term]
  (mapv #(to-clj %) (Util/listToTermArray term)))

(defn pl-list-to-clj-list
  [term]
  (map #(to-clj %) (Util/listToTermArray term)))

(defn pl-atom-list-to-string-list
  [^Term term]
  (into [] (Util/atomListToStringArray term)))

(defn bindings-to-terms
  [^java.util.Map bindings-map]
  (into [] (Util/bindingsToTermArray bindings-map)))

(defn ^java.util.Map term-to-bindings
  [ ^Term term]
  (Util/namevarsToMap term))

(defn ^String solution-to-text
  "convert solution hash map to string."
  [^java.util.Map solution-map]
  (Util/toString solution-map)) 

(defn show-solution
  "Pretty print a solution hash map."
  [^java.util.Map solution-map]
  (doseq [[varname term] solution-map]
    (println varname " = " (to-clj term))
    )) 


;; Exceptions

(defn get-term-from-exception 
  [^PrologException exc]
  (.term exc))

(defmacro do-pl 
  [pl-form exc-form]
  `(try 
     ~pl-form
     (catch jpl.JPLException ~'exc
       ~exc-form)))

;; Atom

(defn build-atom
  [^String s]
  (Atom. s))

(defn build-empty-list
  "Build an empty prolog list."
  []
  (Atom. "[]")) 

(defn is-empty-list?
  "checks for empty prolog list."
  [^Atom a]
  (= (.name a) "[]")) 


;; Variable

(defn build-var
  ([] (Variable.))
  ([^String name]
    (Variable. name)))

(defn get-var-name
  [^Variable var]
  (.name var)) 

(defn var-to-text
  "Pretty print variable." 
  [^Variable var]
  (.toString var)) 

;; JRef

(defn build-jref
  [obj]
  (JRef. obj)) 

(defn jref-to-object
  [^JRef ref]
  (.jrefToObject ref))

(defn get-ref
  [^JRef ref]
  (.ref ref))


;; Integer

(defn build-integer
  [^long l]
  (jpl.Integer. l))


;; Float

(defn build-float
  [^double d]
  (jpl.Float. d))


;; Term types from jpl/fli/Prolog.java

(def VARIABLE jpl.fli.Prolog/VARIABLE)
(def ATOM jpl.fli.Prolog/ATOM)
(def INTEGER jpl.fli.Prolog/INTEGER)
(def FLOAT jpl.fli.Prolog/FLOAT)
(def STRING jpl.fli.Prolog/STRING)
(def COMPOUND jpl.fli.Prolog/COMPOUND)
(def JBOOLEAN jpl.fli.Prolog/JBOOLEAN)
(def JREF jpl.fli.Prolog/JREF)
(def JVOID jpl.fli.Prolog/JVOID)


;; Term

(defn term-to-text
  "Pretty format a term." 
  [^Term term]
  (.toString term))

(defn term-list-to-text
  "Pretty format a list of terms." 
  [terms]
  (Term/toString (into-array Term terms)))

(defn ^String get-name
  [^Term term]
  (.name term))

(defn ^int get-arity
  [^Term term]
  (.arity term))

(defn ^int get-type
  "See term types." 
  [^Term term]
  (.type term))

(defn ^String get-type-name
  [^Term term]
  (.typeName term))

(defn has-functor?
  "Whether the compounds functor has name and arity."
  [^Term term ^String name  ^Long arity]
  (.hasFunctor term name arity)) 

(defn has-functor-integer?
  "Whether the compounds functor is integer and arity."
  [^Term term ^Integer i  ^Long arity]
  (.hasFunctor term i arity)) 

(defn has-functor-double?
  "Whether the compounds functor is double and arity."
  [^Term term ^Double d  ^Long arity]
  (.hasFunctor term d arity)) 


; some special functors
(def list-functor ".")
(def and-functor ",")
(def or-functor ";")
(def if-functor "->")
(def jref-functor "@")

(defn ^Term get-ith-arg
  "get the ith argument (counting from 1)." 
  [^Term c ^long i]
  (.arg c i))

(defn get-args
  "get all arguments." 
  [^Term c]
  (into [] (.args c)))

(defn get-int-value
  [^Term term]
  (.intValue term))

(defn get-long-value
  [^Term term]
  (.longValue term))

(defn get-float-value
  [^Term term]
  (.floatValue term))

(defn get-double-value
  [^Term term]
  (.doubleValue term))

(defn is-atom? 
  [^Term term]
  (.isAtom term)) 

(defn is-compound? 
  [^Term term]
  (.isCompound term)) 

(defn is-float? 
  [^Term term]
  (.isFloat term)) 

(defn is-integer? 
  [^Term term]
  (.isInteger term)) 

(defn is-var? 
  [^Term term]
  (.isVariable term)) 

(defn is-jref?
  [^Term term]
  (.isJRef term))

(defn is-list?
  [term]
  (has-functor? term list-functor 2))

(defn is-and?
  [term]
  (has-functor? term and-functor 2))

(defn is-or?
  [term]
  (has-functor? term or-functor 2))

(defn is-if?
  [term]
  (has-functor? term if-functor 3))

(defn put-params 
  [^Term term ^Term plist]
  (if (instance? Term plist)
    (.putParams term (.toTermArray plist))
    (.putParams term plist)))

(defn length-of-pl-list
  "Iff term is a prolog list, return its length." 
  [^Term term]
  (.listLength term)) 

(defn to-term-array
  "Iff term is a prolog list, return a n array of its succcessive members." 
  [^Term term]
  (.toTermArray term)) 

(defn to-term-vec
  "Iff term is a prolog list, return a clojure vector of its succcessive members." 
  [^Term term]
  (into [] (.toTermArray term))) 

(defn- string-starts-with-upper-case
  [^String s]
   (Character/isUpperCase (.codePointAt s 0))) 


;; Compound

(defn compound
  ([^String name] (compound name [(build-empty-list)]))
  ([^String name terms]
    (Compound. name ^"[Ljpl.Term;" (into-array Term terms))))

(defn compound-with-arity
  [^String name ^Integer arity]
    (Compound. name arity))

(defn set-arg
  "Set the i-th (from 1) arg of a compound."
  [^Compound c ^Integer index ^Term term]
  (.setArg c index term)) 

(defn compound-to-text
  [^Compound c]
  (.toString c))

;; Query

(defn get-goal
  "Returns the term representing the goal of the query." 
  [^Query q]
  (.goal q))

;(defn build-query-from-text
;  "build a new query from prolog source text."
;  [^String text]
;  (Query. text))
;
;(defn build-query-from-text-with-parm
;  "build a new query from source text with a single parameter."
;  [^String text ^Term term] 
;  (Query. text term))
;
;(defn build-query-from-text-with-parms
;  "build a new query from source text with '?'-parameter substitutions."
;  [^String source params]
;  (Query. source ^"[Ljpl.Term;" (into-array Term params)))
;
;(defn build-query-from-term
;  "build a new query from a single term (goal)."
;  [^Term term] (Query. term))

;(defn ^boolean has-solution?
;  "Returns true if the goal is satisfiable." 
;  [^Query q]
;  (.hasSolution q))

(defn get-solution
  "Returns the first solution." 
  [^Query q]
  (.getSolution q))

(defn ^boolean has-more-solutions?
  "Returns true is the query succeeds, otherwise false." 
  [^Query q]
  (.hasMoreSolutions q))

(def more-elements
  "Alias for java.util.Enumeration interface compliance."
  has-more-solutions?) 

(defn get-next-solution
  "Returns the next solution. Check with has-more-solutions? before." 
  [^Query q]
  (.nextSolution q))

(def next-element 
  "Alias for java.util.Enumeration interface compliance."
  get-next-solution) 

;(defn get-one-solution
;  "Returns the first solution, if any." 
;  [^Query q]
;  (.oneSolution q))

;(def get-first-solution
;  "Alias for convenience."
;  get-one-solution)

;(defn get-all-solutions
;  "call the query's goal to exhaustion." 
;  [^Query q]
;  (into [] (.allSolutions q)))

;(defn get-n-solutions
;  "Return the first n solutions of the the query's goal." 
;  [^Query q ^long n]
;  (into [] (.nSolutions q n)))

(defn get-subst-with-name-vars
  "Returns the first solution with name-variable-substitutions.
   Assumes that the query's last argument is a variable which will be bound
   to a [name=Var,...] dictionary." 
  [^Query q]
  (.getSubstWithNameVars q))

(defn get-var 
  "Returns the 'raw' value of a varaiable."
  [solution var]
  (if (instance? Variable var)
    (get solution (get-var-name var))
    (get solution var)))

(defn open-query
  [^Query q]
  (.open q))

(defn close-query
  "You may close an open query before its solutions are exhausted." 
  [^Query q]
  (.close q))

(defn is-open?
  "Returns true iff the query is open." 
  [^Query q]
  (.isOpen q))

(defn query-to-text
  "A crude string representation of a query." 
  [^Query q]
  (.toString q))

(defn show-query
  "A representation of a query." 
  [^Query q]
  (to-clj (get-goal q)))

;(defn ^boolean run-has-solution-from-term?
;  "Ad hoc query with term. Returns true if the goal is satisfiable." 
;  [^Term term]
;  (Query/hasSolution term))
;
;(defn ^boolean run-has-solution-from-text?
;  "Ad hoc query with text source goal. Returns true if the goal is satisfiable." 
;  [^String text]
;  (Query/hasSolution text))

;(defn ^boolean run-has-solution-from-text-with-params?
;  "Ad hoc query with text source goal and parameters. Returns true if the goal is satisfiable." 
;  [^String source params]
;  (Query/hasSolution source (into-array Term params)))

;(defn run-query-from-term
;  "Ad hoc query with term goal. Returns only first solution, if any."
;  [^Term term]
;  (Query/oneSolution term)) 

;(defn run-query-from-text
;  "Ad hoc query with text source goal. Returns only first solution, if any."
;  [^String text]
;  (Query/oneSolution text)) 

;(defn run-query-from-source-with-params
;  "Ad hoc query with text source goal and parameters. Returns only first solution, if any."
;  [^String text params]
;  (Query/oneSolution text (into-array Term params))) 

;(defn run*-query-from-term
;   "Ad hoc query with term goal. Returns all solutions."
;  [^Term term]
;  (into [] (Query/allSolutions term))) 

;(defn run*-query-from-text
;   "Ad hoc query with text source goal. Returns all solutions."
;  [^String text]
;  (into [] (Query/allSolutions text))) 

;(defn run*-query-from-text-with-params
;   "Ad hoc query with text source goal and parameters. Returns all solutions."
;  [^String text params]
;  (into [] (Query/allSolutions text (into-array Term params)))) 

;(defn run-n-query-from-term
;   "Ad hoc query with term goal. Returns the given number of solutions."
;  [^long n ^Term term]
;  (into [] (Query/nSolutions term n))) 

;(defn run-n-query-from-text
;   "Ad hoc query with text source goal. Returns the given number of solutions."
;  [^long n ^String text]
;  (into [] (Query/nSolutions text n))) 

;(defn run-n-query-from-text-with-params
;   "Ad hoc query with text source goal and parameters. Returns the given number of solutions."
;  [^long n ^String text params]
;  (into [] (Query/nSolutions text (into-array Term params) n))) 


;; Convenience methods

(defn consult 
  "Consult and run a prolog source file." 
  [file]
   (run-q-1 (str "consult('"  file "')"))) 

(defn halt 
  "Stops the prolog engine." 
  []
   (run-q-1 "halt")) 


;(defn get-pl-term-value
;  "Try to get the 'raw' value of a term, e.g. the (long) value of a jpl.Integer."
;  [^jpl.Term term]
;  (cond 
;    (nil? term) nil
;    (seq? term) term
;    (instance? Atom term) (get-name term)
;    (instance? jpl.Integer term) (get-long-value term)
;    (instance? jpl.Float term)  (get-float-value term)   
;    (instance? JRef term) (get-ref term)
;    :else term))
;
;(defn get-value
;  "Get the Clojure value of a variable returned by the solution."
;  [solution var-name]
;  (when-let [value (get-var solution var-name)]
;    (to-clj value))) 


;; protocol implementations

(extend-protocol ICljToPrologConversion
  
  nil
  ;; "old LISPy": nil becomes the empty prolog list!
  (to-pl [data]
    (build-empty-list))
  clojure.lang.PersistentList$EmptyList
  (to-pl [data]
    (build-empty-list))
  
  String
  (to-pl [data]
    (if (string-starts-with-upper-case data)
      (build-var data) 
      (build-atom data)))
  
  clojure.lang.IPersistentVector
  (to-pl [data] 
    (seq-to-pl-list (map to-pl data)))
  
  clojure.lang.IPersistentList
  (to-pl [data] 
    (seq-to-pl-list (map to-pl data)))
  
  clojure.lang.IPersistentMap
  (to-pl [data] 
    (seq-to-pl-list (map to-pl data)))
  
  java.lang.Integer
  (to-pl [data] (build-integer data))
  
  java.lang.Long
  (to-pl [data] (build-integer data))
  
  java.lang.Float
  (to-pl [data] (build-float data))
  
  java.lang.Double
  (to-pl [data] (build-float data))
  
  Term
  (to-pl [data] data)
  
  Object
  (to-pl [data] (build-jref data)))

(extend-protocol IPrologToCljConversion
  
  nil
  (to-clj [data])
  
  jpl.Integer
  (to-clj [data] (get-int-value data))
  
  jpl.Float
  (to-clj [data] (get-float-value data))
  
  jpl.Atom
  (to-clj [data] 
    (if (is-empty-list? data) 
      []
      (get-name data)))
  
  jpl.Variable
  (to-clj [data] (get-var-name data))
  
  jpl.JRef
  (to-clj [data] (get-ref data))
  
  jpl.Term
  (to-clj [data] 
    (if (is-list? data)
      (pl-list-to-vec data)
      data))
  
  Object
  (to-clj [data] data))

(extend-protocol IPrologQuery
  
  String
  (build-q [text]
    (Query. text))
  (build-q-with-param [text ^Term param]
    (Query. text param))
  (build-q-with-params [text params]
    (Query. text ^"[Ljpl.Term;" (into-array Term params)))
  (has-q-solution? [text]
    (Query/hasSolution text))
  (has-q-solution-with-params? [text params]
    (Query/hasSolution text (into-array Term params)))
  (run-q-1 [text]
    (Query/oneSolution text))
  (run-q-1-with-params [text params]
     (Query/oneSolution text (into-array Term params)))
  (run-q [text]
    (into [] (Query/allSolutions text)))
  (run-q-with-params [text params]
    (into [] (Query/allSolutions text (into-array Term params))))
  (run-q-n [text n]
    (into [] (Query/nSolutions text ^Long n)))
  (run-q-n-with-params [text params n]
    (into [] (Query/nSolutions text (into-array Term params) n)))
  
  jpl.Term
  (build-q [term]
    (Query. term))
  (has-q-solution? [term]
    (Query/hasSolution term))
  (run-q-1 [term]
    (Query/oneSolution term))
  (run-q [term]
    (into [] (Query/allSolutions term)))
  (run-q-n [n term]
     (into [] (Query/nSolutions ^Term term ^Long n)))
  
  jpl.Query
  (has-q-solution? [q]
    (.hasSolution q))
  (run-q-1 [q]
    (.oneSolution q))
  (run-q [q]
    (into [] (.allSolutions q)))
  (run-q-n [q n]
    (into [] (.nSolutions q n)))
)

(extend-protocol IPrologSolution
  
  nil
  (get-value [soln var])
  
  java.util.Map
  (get-value [soln var]
    (when-let [value (get-var soln var)]
      (to-clj value)))
)

(extend-protocol IPrologSourceText
  
  nil
  (text-to-pl [text]
    (build-empty-list))
  (pl-to-text [term]
    "")
   (pl-to-inspection-text [text]
     (.debugString ^Atom (build-empty-list)))
   
  String
  (text-to-pl [text]
    (Util/textToTerm text))
  (pl-to-inspection-text [text]
     (.debugString (Util/textToTerm text)))
  
  jpl.Term
  (pl-to-text [term]
    (.toString term))
  (pl-to-inspection-text [term]
     (.debugString term))
  
  jpl.Query
  (pl-to-text [query]
    (.toString query))
  (pl-to-inspection-text [query]
     (.debugString query))
  
  java.util.Map
  (pl-to-text [soln]
    (Util/toString soln))
   (pl-to-inspection-text [soln]
    (Util/toString soln))
 )


;; EOF
