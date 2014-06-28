(ns 
  ^{:author "Peter Feldtmann"
    :doc "A Clojure SWI-Prolog bridge.
          Call prolog goals directly from clojure code."}
  clj.swipl
  (:import [jpl JPL Atom Compound JPLException
            PrologException Query Term
            Util Variable JRef])
  )

; to install the SWI-Prolog java bridge jpl.jar:
; get and install the leiningen plugin 'localrepo',
; then do: lein localrepo install 'path-to-swi-prolog'/lib/jpl.jar jpl 3.1.4-alpha

(set! *warn-on-reflection* true)

;; forward references
(declare get-pl-term-value)

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

(defn make-var
  ([] (Variable.))
  ([^String name]
    (Variable. name)))

(defn get-var-name
  [^Variable var]
  (.name var)) 

(defn show-var
  "Pretty print variable." 
  [^Variable var]
  (.toString var)) 

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

(defn show-term
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
  "See term types." 
  [^Term term]
  (.type term))

(defn ^String get-type-name
  [^Term term]
  (.typeName term))

(defn has-functor?
  "Whether the compounds functor has name and arity."
  [^Term term ^String name  ^long arity]
  (.hasFunctor term name arity)) 

(defn has-functor?
  "Whether the compounds functor is integer and arity."
  [^Term term ^Integer i  ^long arity]
  (.hasFunctor term i arity)) 

(defn has-functor?
  "Whether the compounds functor is double and arity."
  [^Term term ^double d  ^long arity]
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
  (.args c))

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

(defn make-term 
  [x]
  (cond 
    (list? x) (seq-to-pl-list (mapv make-term x))
    (vector? x) (seq-to-pl-list (mapv make-term x)) 
    (instance? Compound x) x
    (instance? java.lang.Integer x) (make-integer x)
    (instance? java.lang.Long x) (make-integer x)
    (instance? java.lang.Float x) (make-float x)
    (instance? java.lang.Double x) (make-float x)
    (instance? String x) 
       (if (string-starts-with-upper-case x) (make-var x) 
                                             (make-atom x) )
    :else (make-jref x)))


;; Compound

(defn make-compound
  ([^String name] (make-compound name [(make-empty-list)]))
  ([^String name terms]
    (Compound. name ^objects (into-array Term terms))))

(defn make-compound-with-arity
  [^String name ^Integer arity]
    (Compound. name arity))

(defn set-arg
  "Set the i-th (from 1) arg of a compound."
  [^Compound c ^Integer index ^Term term]
  (.setArg c index term)) 


;; Query

(defn get-goal
  "Returns the term representing the goal of the query." 
  [^Query q]
  (.goal q))

(defn make-query-from-text
  "build a new query from prolog source text."
  [^String text]
  (Query. text))

(defn make-query-from-text-with-parm
  "build a new query from source text with a single parameter."
  [^String text ^Term term] 
  (Query. text term))

(defn make-query-from-text-with-parms
  "build a new query from source text with '?'-parameter substitutions."
  [^String source params]
  (Query. source ^objects (into-array Term params)))

(defn make-query-from-term
  "build a new query from a single term (goal)."
  [^Term term] (Query. term))

(defn ^boolean has-solution?
  "Returns true if the goal is satisfiable." 
  [^Query q]
  (.hasSolution q))

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

(defn get-one-solution
  "Returns the first solution, if any." 
  [^Query q]
  (.oneSolution q))

(def get-first-solution
  "Alias for convenience."
  get-one-solution)

(defn get-all-solutions
  "call the query's goal to exhaustion." 
  [^Query q]
  (into [] (.allSolutions q)))

(defn get-n-solutions
  "Return the first n solutions of the the query's goal." 
  [^Query q ^long n]
  (into [] (.nSolutions q n)))

(defn get-subst-with-name-vars
  "Returns the first solution with name-variable-substitutions.
   Assumes that the query's last argument is a variable which will be bound
   to a [name=Var,...] dictionary." 
  [^Query q]
  (.getSubstWithNameVars q))

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

(defn ^boolean run-has-solution-from-text?
  "Ad hoc query with text source goal. Returns true if the goal is satisfiable." 
  [^String text]
  (Query/hasSolution text))

(defn ^boolean run-has-solution-from-text-with-params?
  "Ad hoc query with text source goal and parameters. Returns true if the goal is satisfiable." 
  [^String source params]
  (Query/hasSolution source (into-array Term params)))

(defn run-query-from-term
  "Ad hoc query with term goal. Returns only first solution, if any."
  [^Term term]
  (Query/oneSolution term)) 

(defn run-query-from-text
  "Ad hoc query with text source goal. Returns only first solution, if any."
  [^String text]
  (Query/oneSolution text)) 

(defn run-query-from-source-with-params
  "Ad hoc query with text source goal and parameters. Returns only first solution, if any."
  [^String text params]
  (Query/oneSolution text (into-array Term params))) 

(defn run*-query-from-term
   "Ad hoc query with term goal. Returns all solutions."
  [^Term term]
  (into [] (Query/allSolutions term))) 

(defn run*-query-from-text
   "Ad hoc query with text source goal. Returns all solutions."
  [^String text]
  (into [] (Query/allSolutions text))) 

(defn run*-query-from-text-with-params
   "Ad hoc query with text source goal and parameters. Returns all solutions."
  [^String text params]
  (into [] (Query/allSolutions text (into-array Term params)))) 

(defn run-n-query-from-term
   "Ad hoc query with term goal. Returns the given number of solutions."
  [^long n ^Term term]
  (into [] (Query/nSolutions term n))) 

(defn run-n-query-from-text
   "Ad hoc query with text source goal. Returns the given number of solutions."
  [^long n ^String text]
  (into [] (Query/nSolutions text n))) 

(defn run-n-query-from-text-with-params
   "Ad hoc query with text source goal and parameters. Returns the given number of solutions."
  [^long n ^String text params]
  (into [] (Query/nSolutions text (into-array Term params) n))) 


;; Convenience methods

(defn consult 
  "Consult and run a prolog source file." 
  [file]
   (run-query-from-text (str "consult('"  file "')"))) 

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
    :else term))

(defn get-value
  "Get the 'raw' prolog value of a variable returned by the solution."
  [var-name solution]
  (when-let [v (get-var var-name solution)]
    (get-pl-term-value v))) 

;; EOF
