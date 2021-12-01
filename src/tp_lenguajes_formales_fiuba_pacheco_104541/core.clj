(ns tp-lenguajes-formales-fiuba-pacheco-104541.core)
  
(require '[tp-lenguajes-formales-fiuba-pacheco-104541.probando_exports :refer :all]
         '[tp-lenguajes-formales-fiuba-pacheco-104541.scheme :refer :all]
)

(declare ejemplo)
(declare es-el-doble?)
(declare spyy)

(defn -main
  "Ejemplo de Proyecto en Clojure"
  [& args]
  (println "Hola, soy un proyecto en Clojure")
  (println (sumar_pach 2 31121))
  (ejemplo))

(defn ejemplo []
  (let [mensaje-1 (do (print "Ingrese un numero: ") (flush)),
        n1 (read),
        mensaje-2 (do (print "Ingrese el doble del numero anterior: ") (flush)),
        n2 (read),
        salida (println (str "Ud." (if (es-el-doble? n1 n2) " " " no ") "sabe calcular!"))]
    (do (print "Presione Enter...") (flush) (read-line) (read-line) 'Chau!)))

(defn es-el-doble? [a b]
  (= (* 2 a) b))

(defn spyy
  ([x] (do (prn x) x))
  ([msg x] (do (print msg) (print ": ") (prn x) x)))