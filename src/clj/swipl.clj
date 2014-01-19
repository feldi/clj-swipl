(ns 
  ^{:author "Peter Feldtmann"
    :doc "A Clojure SWI-Prolog bridge.
          Call prolog goals directly from clojure code."}
  clj.swipl
  (:import [jpl JPL Atom Compound JPLException
            JRef PrologException Query Term
            Util Variable])
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

(defn seq-to-pl-list
  [terms]
  (Util/termArrayToList (into-array Term terms)))

(defn length-of-pl-list
  [term]
  (Util/listToLength term))

(defn pl-list-to-vec
  [term]
  (mapv #(get-pl-term-value %) (Util/listToTermArray term)))

(defn pl-list-to-clj-list
  [term]
  (map #(get-pl-term-value %) (Util/listToTermArray term)))

(defn bindings-to-terms
  [bindings-map]
  (Util/bindingsToTermArray bindings-map))


;; Atom

(defn make-atom
  [^String s]
  (Atom. s))

(defn ^int get-atom-type
  [^Atom a]
  (.type a))

(defn ^String get-atom-type-name
  [^Atom a]
  (.typeName a))


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

(defn get-long-value
  [^jpl.Integer i]
  (.longValue i))

(defn get-int-value
  [^jpl.Integer i]
  (.intValue i))


;; Float

(defn make-float
  [^double d]
  (jpl.Float. d))

(defn get-double-value
  [^jpl.Float f]
  (.doubleValue f))

(defn get-float-value
  [^jpl.Float f]
  (.floatValue f))


;; Term

(defn show-term
  [^Term term]
  (.toString term))

(defn ^String get-name
  [^Term term]
  (.name term))

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
  [name terms]
  (Compound. name (into-array Term terms)))

(defn ^String get-name
  [^Compound c]
  (.name c))

(defn ^int get-arity
  [^Compound c]
  (.arity c))

(defn ^Term get-ith-arg
  [^Compound c înt i]
  (.arg c i))


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

(defn has-solution
  "Returns true or false." 
  [^Query q]
  (.hasSolution q))

(defn one-solution
  [^Query q]
  (into {} (.oneSolution q)))

(def first-solution one-solution)

(defn all-solutions
  [^Query q]
  (mapv #(into {} %) (.allSolutions q)))

(defn get-var 
  [var solution]
  (if (instance? Variable var)
    (get solution (get-var-name var))
    (get solution var)))

(defn close-query
  [^Query q]
  (.close q))

(defn show-query
  [^Query q]
  (.toString q))

(defn run-query-from-source
  [^String source]
  (into {} (Query/oneSolution source))) 

(defn run*-query-from-source
  [^String source]
  (mapv #(into {} %) (Query/allSolutions source))) 


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
