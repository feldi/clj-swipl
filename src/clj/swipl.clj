(ns 
  ^{:author "Peter Feldtmann"
    :doc "A Clojure SWI-Prolog bridge.
          Call prolog goals directly from clojure code."}
  clj.swipl
  (:import [jpl JPL Atom Compound JPLException
            PrologException Query Term
            Util Variable JRef])
  )

; to install the swi-prolog java bridge jpl.jar:
; get and install the leiningen plugin 'localrepo',
; then do: lein localrepo install 'path-to-swi-prolog'/lib/jpl.jar jpl 3.1.4-alpha

#_(set! *warn-on-reflection* true)

(declare get-pl-term-value)

;; JPL main class

(defn get-version-as-string
  "Get jpl version." 
  []
  (JPL/version_string))

(defn init
  "Explicit initialization. Most often not needed."
  []
  (JPL/init))

(defn init-with-args
  "Explicit initialization with list of arguments, 
   provided as a vector. Most often not needed."
  [args-list]
  (JPL/init (into-array String args-list)))

(defn get-default-init-args
  []
  (into [] (JPL/getDefaultInitArgs)))

(defn set-default-init-args!
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
  (Util/intArrayToList (into-array int list-of-ints)))

(defn pl-list-to-length
  [term]
  (Util/listToLength term))

(defn pl-list-to-vec
  [term]
  (mapv #(get-pl-term-value %) (Util/listToTermArray term)))

(defn pl-list-to-clj-list
  [term]
  (map #(get-pl-term-value %) (Util/listToTermArray term)))

(defn pl-atom-list-to-string-list
  [^Term term]
  (into [] (Util/atomListToStringArray term)))

(defn bindings-to-terms
  [^java.util.Map bindings-map]
  (into [] (Util/bindingsToTermArray bindings-map)))

(defn ^java.util.Map term-to-bindings
  [ ^Term term]
  (Util/namevarsToMap term))

(defn ^String show-solution
  "Pretty print a solution hash map."
  [^java.util.Map solution-map]
  (Util/toString solution-map)) 


;; Exceptions

(defn get-term-from-exception 
  [^PrologException exc]
  (.term exc))


;; Atom

(defn make-atom
  [^String s]
  (Atom. s))

(defn make-empty-list
  "Build an empty prolog list."
  []
  (make-atom "[]")) 

;; Variable

(defn make-variable
  ([]
    (Variable.))
  ([^String name]
    (Variable. name)))

(defn get-var-name
  [^Variable var]
  (.name var)) 

;; JRef

(defn make-jref
  [obj]
  (JRef. obj)) 

(defn jref-to-object
  [^JRef ref]
  (.jrefToObject ref))

(defn get-ref
  [^JRef ref]
  (.ref ref))


;; Integer

(defn make-integer
  [^long l]
  (jpl.Integer. l))


;; Float

(defn make-float
  [^double d]
  (jpl.Float. d))


;; Term

(defn show
  "Pretty print a term." 
  [^Term term]
  (.toString term))

(defn show-term-list
  "Pretty print a list of terms." 
  [terms]
  (Term/toString (into-array Term terms)))

(defn ^String get-name
  [^Term term]
  (.name term))

(defn ^int get-arity
  [^Term term]
  (.arity term))

(defn ^int get-type
  [^Term term]
  (.type term))

(defn ^String get-type-name
  [^Term term]
  (.typeName term))

(defn has-functor
  "Whether the compounds functor has name and arity."
  [^Term term ^String name  ^long arity]
  (.hasFunctor term name arity)) 

(defn ^Term get-ith-arg
  "get the ith argument (counting from 1)." 
  [^Term c ^long i]
  (.arg c i))

(defn get-args
  "get all arguments." 
  [^Term c]
  (.args c))

(defn get-int-value
  [^Term term]
  (.intValue term))

(defn get-long-value
  [^Term term]
  (.longValue term))

(defn get-double-value
  [^Term term]
  (.doubleValue term))

(defn get-float-value
  [^Term term]
  (.floatValue term))

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

(defn is-variable? 
  [^Term term]
  (.isVariable term)) 

(defn put-params 
  [^Term term ^Term plist]
  (.putParams term plist))

(defn put-params-list 
  [^Term term ps]
  (.putParams term ps))

(defn length-of-pl-list
  "Iff term is a prolog list, return its length." 
  [^Term term]
  (.listLength term)) 

(defn to-term-array
  "Iff term is a prolog list, return a vector of its succcessive members." 
  [^Term term]
  (into [] (.toTermArray term))) 

(defn make-term 
  [x]
  (cond 
    (list? x) (seq-to-pl-list (mapv make-term x))
    (vector? x) (seq-to-pl-list (mapv make-term x)) 
    (instance? java.lang.Integer x) (make-integer x)
    (instance? java.lang.Long x) (make-integer x)
    (instance? java.lang.Float x) (make-float x)
    (instance? java.lang.Double x) (make-float x)
    (instance? Variable x) x
    (instance? String x) (make-atom x)
    :else (make-jref x)
    ))


;; Compound

(defn make-compound
  ([name] )
  ([name terms]
  (Compound. name (into-array Term terms))))


;; Query

(defn make-query-from-source
  "build a new query from prolog source text."
  [^String source]
  (Query. source))

(defn make-query-from-term
  "build a new query from a single term (goal)."
  ([^Term term] (Query. term))
  ([^String text ^Term term] (Query. text term)))

(defn make-query-from-terms
  "build a new query from a list of terms."
  [^String source  terms]
  (Query. source (into-array Term  terms)))

(defn make-query-with-parms
  "build a new query from text with '?'-parameter substitutions."
  [^String source args]
  (Query. source (into-array Term  args)))

(defn get-goal
  "Returns the term representing the goal of the query." 
  [^Query q]
  (.goal q))

(defn ^boolean has-solution?
  "Returns true if the goal is satisfiable." 
  [^Query q]
  (.hasSolution q))

(defn get-solution
  "Returns the first solution." 
  [^Query q]
  (.getSolution q))

(defn get-subst-with-name-vars
  "Returns the first solution with name-variable-substitutions.
   Assumes that the query's last argument is a variable which will be bound
   to a [name=Var,...] dictionary." 
  [^Query q]
  (.getSubstWithNameVars q))

(defn ^boolean has-more-solutions?
  "Returns true is the querry succeeds, otherwise false." 
  [^Query q]
  (.hasMoreSolutions q))

(def more-elements
  "Alias for java.util.Enumeration interface compliance."
  has-more-solutions?) 

(defn get-next-solution
  "Returns the next solution. Check with hasMoreSolutions before." 
  [^Query q]
  (.nextSolution q))

(def next-element 
  "Alias for java.util.Enumeration interface compliance."
  get-next-solution) 

(defn get-one-solution
  "Returns the first solution, if any." 
  [^Query q]
  (into {} (.oneSolution q)))

(def get-first-solution
  "Alias for convenience."
  get-one-solution)

(defn get-all-solutions
  "call the query's goal to exhaustion." 
  [^Query q]
  (mapv #(into {} %) (.allSolutions q)))

(defn get-n-solutions
  "Return the first n solutions of the the query's goal." 
  [^Query q ^long n]
  (mapv #(into {} %) (.nSolutions q n)))

(defn get-var 
  [var solution]
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

(defn show-query
  "A crude string representation of a query." 
  [^Query q]
  (.toString q))

(defn ^boolean run-has-solution-from-term?
  "Ad hoc query with term. Returns true if the goal is satisfiable." 
  [^Term term]
  (Query/hasSolution term))

(defn ^boolean run-has-solution-from-source?
  "Ad hoc query with text source goal. Returns true if the goal is satisfiable." 
  [^String source]
  (Query/hasSolution source))

(defn ^boolean run-has-solution-from-source-with-params?
  "Ad hoc query with text source goal and parameters. Returns true if the goal is satisfiable." 
  [^String source params]
  (Query/hasSolution source (into-array Term params)))

(defn run-query-from-term
  "Ad hoc query with term goal. Returns only first solution, if any."
  [^Term term]
  (into {} (Query/oneSolution term))) 

(defn run-query-from-source
  "Ad hoc query with text source goal. Returns only first solution, if any."
  [^String source]
  (into {} (Query/oneSolution source))) 

(defn run-query-from-source-with-params
  "Ad hoc query with text source goal and parameters. Returns only first solution, if any."
  [^String source params]
  (into {} (Query/oneSolution source (into-array Term params)))) 

(defn run*-query-from-term
   "Ad hoc query with term goal. Returns all solutions."
  [^Term term]
  (mapv #(into {} %) (Query/allSolutions term))) 

(defn run*-query-from-source
   "Ad hoc query with text source goal. Returns all solutions."
  [^String source]
  (mapv #(into {} %) (Query/allSolutions source))) 

(defn run*-query-from-source-with-params
   "Ad hoc query with text source goal and parameters. Returns all solutions."
  [^String source params]
  (mapv #(into {} %) (Query/allSolutions source (into-array Term params)))) 

(defn run-n-query-from-term
   "Ad hoc query with term goal. Returns the given number of solutions."
  [^long n ^Term term]
  (mapv #(into {} %) (Query/nSolutions term n))) 

(defn run-n-query-from-source
   "Ad hoc query with text source goal. Returns the given number of solutions."
  [^long n ^String source]
  (mapv #(into {} %) (Query/nSolutions source n))) 

(defn run-n-query-from-source-with-params
   "Ad hoc query with text source goal and parameters. Returns the given number of solutions."
  [^long n ^String source params]
  (mapv #(into {} %) (Query/nSolutions source (into-array Term params) n))) 


;; Convenience methods

(defn consult 
  [file]
   (run-query-from-source (str "consult('"  file "')"))) 

(defn get-pl-term-value
  "Try to get the value of a term, e.g. the (long) value of a jpl.Integer."
  [^jpl.Term term]
  (cond 
    (nil? term) nil
    (seq? term) term
    (instance? Atom term) (get-name term)
    (instance? jpl.Integer term) (get-long-value term)
    (instance? jpl.Float term)  (get-float-value term)   
    (instance? JRef term) (get-ref term)
    :else term
    ))

(defn get-pl-value
  "Get the 'raw' prolog value of a variable returned by the solution."
  [var-name solution]
  (when-let [v (get-var var-name solution)]
    (get-pl-term-value v)) 
  ) 

(defn get-value
  "Get the value of a variable returned by the solution."
  [var-name solution]
  (when-let [v (get-var var-name solution)]
    (get-pl-term-value v)) 
  ) 
