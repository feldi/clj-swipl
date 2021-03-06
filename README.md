# clj-swipl

A Clojure library designed to call SWI-Prolog goals directly from clojure code.

If you find it to hard to translate your prolog projects to core.logic, 
this might be your choice.

## Installation Preparations

Be sure you fulfill the JPL 3.x installation prerequisites as desribed unter [http://www.swi-prolog.org/packages/jpl/installation.html].

If you want to work yourself on this project, you have to install the SWI-Prolog java bridge jpl.jar in your local maven repository:
get and install the leiningen plugin 'localrepo',then do:

	lein localrepo install 'path-to-swi-prolog'/lib/jpl.jar jpl 3.1.4-alpha


## Usage

put something like
```clojure
	(:require [clj.swipl :as pl])
```
in your namespace declaration.


Check the examples in 'swipl_demo.clj'.

Here is one of them:
```clojure 
(defn demo-all-solutions
  []
  (let [query     (pl/make-query-from-text "X is 1; X is 2; X is 3")
        solutions (pl/get-all-solutions query)]
    (println "demo-all-solutions: all solutions to " (pl/show-query query)  " ==> " 
             (map pl/show-solution solutions))))

(demo-all-solutions)
```	 
			 
## License

Copyright © 2014 Peter Feldtmann

Distributed under the Eclipse Public License, the same as Clojure.
