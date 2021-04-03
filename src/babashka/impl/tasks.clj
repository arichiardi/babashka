(ns babashka.impl.tasks
  (:require [babashka.impl.common :refer [ctx bb-edn]]
            [babashka.impl.deps :as deps]
            [babashka.process :as p]
            [sci.core :as sci]))

(def sci-ns (sci/create-ns 'babashka.tasks nil))

(defn- exit-non-zero [proc]
  (when-let [exit-code (some-> proc deref :exit)]
    (when (not (zero? exit-code))
      (System/exit exit-code))))

(defn shell [cmd & args]
  (exit-non-zero (p/process (into (p/tokenize cmd) args) {:inherit true})))

(defn clojure [cmd & args]
  (exit-non-zero (deps/clojure (into (p/tokenize cmd) args))))

(defn run [task]
  (let [task (get-in @bb-edn [:tasks task])]
    (when task
      (sci/eval-form @ctx task))))

(def tasks-namespace
  {'shell (sci/copy-var shell sci-ns)
   'clojure (sci/copy-var clojure sci-ns)
   'run (sci/copy-var run sci-ns)})
