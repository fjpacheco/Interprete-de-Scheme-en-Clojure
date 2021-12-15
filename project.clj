(defproject interprete_scheme "1.0"
  :description "Interprete de Scheme en Clojure - Pacheco, Federico Jose 104541 - Lenguajes Formales 75.14 - FIUBA"
  :url "https://github.com/fjpacheco/Interprete-de-Scheme-en-Clojure"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.0"]]
  :repl-options {:init-ns interprete_scheme.main}
  :main interprete_scheme.main)