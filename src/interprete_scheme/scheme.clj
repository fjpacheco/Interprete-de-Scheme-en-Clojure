(ns interprete_scheme.scheme)

(require '[clojure.string :as st :refer [blank? starts-with? ends-with? lower-case]]
  '[clojure.java.io :refer [delete-file reader]]
  '[clojure.walk :refer [postwalk postwalk-replace]])

(defn spy
([x] (do (prn x) x))
([msg x] (do (print msg) (print ": ") (prn x) x))
)

; Funciones principales
(declare repl)
(declare evaluar)
(declare aplicar)

; Funciones secundarias de evaluar
(declare evaluar-if)
(declare evaluar-or)
(declare evaluar-cond)
(declare evaluar-eval)
(declare evaluar-exit)
(declare evaluar-load)
(declare evaluar-set!)
(declare evaluar-quote)
(declare evaluar-define)
(declare evaluar-lambda)
(declare evaluar-escalar)

; Funciones secundarias de aplicar
(declare aplicar-lambda)
(declare aplicar-funcion-primitiva)

; Funciones primitivas
(declare fnc-car)
(declare fnc-cdr)
(declare fnc-env)
(declare fnc-not)
(declare fnc-cons)
(declare fnc-list)
(declare fnc-list?)
(declare fnc-read)
(declare fnc-mayor)
(declare fnc-menor)
(declare fnc-null?)
(declare fnc-sumar)
(declare fnc-append)
(declare fnc-equal?)
(declare fnc-length)
(declare fnc-restar)
(declare fnc-display)
(declare fnc-newline)
(declare fnc-reverse)
(declare fnc-mayor-o-igual)

; Funciones auxiliares

(declare buscar)
(declare error?)
(declare igual?)
(declare imprimir)
(declare cargar-arch)
(declare revisar-fnc)
(declare revisar-lae)
(declare leer-entrada)
(declare actualizar-amb)
(declare restaurar-bool)
(declare generar-nombre-arch)
(declare nombre-arch-valido?)
(declare controlar-aridad-fnc)
(declare proteger-bool-en-str)
(declare verificar-parentesis)
(declare generar-mensaje-error)
(declare aplicar-lambda-simple)
(declare aplicar-lambda-multiple)
(declare evaluar-clausulas-de-cond)
(declare evaluar-secuencia-en-cond)


; REPL (read–eval–print loop).
; Aridad 0: Muestra mensaje de bienvenida y se llama recursivamente con el ambiente inicial.
; Aridad 1: Muestra > y lee una expresion y la evalua. El resultado es una lista con un valor y un ambiente. 
; Si la 2da. posicion del resultado es nil, devuelve 'Goodbye! (caso base de la recursividad).
; Si no, imprime la 1ra. pos. del resultado y se llama recursivamente con la 2da. pos. del resultado. 
(defn repl
  "Inicia el REPL de Scheme."
  ([]
    (println "Interprete de Scheme en Clojure")
    (println "Trabajo Practico de 75.14/95.48 - Lenguajes Formales 2021") (prn)
    (println "Inspirado en:")
    (println "  SCM version 5f2.")                        ; https://people.csail.mit.edu/jaffer/SCM.html
    (println "  Copyright (C) 1990-2006 Free Software Foundation.") (prn) (flush)
    (repl (list 'append 'append 'car 'car 'cdr 'cdr 'cond 'cond 'cons 'cons 'define 'define
            'display 'display 'env 'env 'equal? 'equal? 'eval 'eval 'exit 'exit
            'if 'if 'lambda 'lambda 'length 'length 'list 'list 'list? 'list? 'load 'load
            'newline 'newline 'nil (symbol "#f") 'not 'not 'null? 'null? 'or 'or 'quote 'quote
            'read 'read 'reverse 'reverse 'set! 'set! (symbol "#f") (symbol "#f")
            (symbol "#t") (symbol "#t") '+ '+ '- '- '< '< '> '> '>= '>=)))
          
  ([amb]
    (print "> ") (flush)
    (try
      (let [renglon (leer-entrada)]                       ; READ
        (if (= renglon "")
            (repl amb)
            (let [str-corregida (proteger-bool-en-str renglon),
                  cod-en-str (read-string str-corregida),
                  cod-corregido (restaurar-bool cod-en-str),
                  res (evaluar cod-corregido amb)]     ; EVAL
                  (if (nil? (second res))              ;   Si el ambiente del resultado es `nil`, es porque se ha evaluado (exit)
                      'Goodbye!                        ;   En tal caso, sale del REPL devolviendo Goodbye!.
                      (do (imprimir (first res))       ; PRINT
                          (repl (second res)))))))     ; LOOP (Se llama a si misma con el nuevo ambiente)
    (catch Exception e                                  ; PRINT (si se lanza una excepcion)
                (imprimir (generar-mensaje-error :error (get (Throwable->map e) :cause)))
                (repl amb)))))                        ; LOOP (Se llama a si misma con el ambiente intacto)


(defn evaluar
  "Evalua una expresion `expre` en un ambiente. Devuelve un lista con un valor resultante y un ambiente."
  [expre amb]
  (if (and (seq? expre) (or (empty? expre) (error? expre))) ; si `expre` es () o error, devolverla intacta
      (list expre amb)                                      ; de lo contrario, evaluarla
      (cond
        (not (seq? expre))                (evaluar-escalar expre amb)
        (igual? (first expre) 'define)    (evaluar-define expre amb)
        (igual? (first expre) 'exit)      (evaluar-exit expre amb)
        (igual? (first expre) 'if)        (evaluar-if expre amb)
        (igual? (first expre) 'or)        (evaluar-or expre amb)
        (igual? (first expre) 'cond)      (evaluar-cond expre amb)
        (igual? (first expre) 'eval)      (evaluar-eval expre amb)
        (igual? (first expre) 'exit)      (evaluar-exit expre amb)
        (igual? (first expre) 'load)      (evaluar-load expre amb)
        (igual? (first expre) 'set!)      (evaluar-set! expre amb)
        (igual? (first expre) 'quote)     (evaluar-quote expre amb)
        (igual? (first expre) 'lambda)    (evaluar-lambda expre amb)

         ;
         ;
         ;
         ; Si la expresion no es la aplicacion de una funcion (es una forma especial, una macro...) debe ser evaluada
         ; por una funcion de Clojure especifica debido a que puede ser necesario evitar la evaluacion de los argumentos
         ;
         ;
         ;

	    	  :else (let [res-eval-1 (evaluar (first expre) amb),
             						 res-eval-2 (reduce (fn [x y] (let [res-eval-3 (evaluar y (first x))] (cons (second res-eval-3) (concat (next x) (list (first res-eval-3)))))) (cons (list (second res-eval-1)) (next expre)))]
					              	(aplicar (first res-eval-1) (next res-eval-2) (first res-eval-2))))))




(defn aplicar
"Aplica la funcion `fnc` a la lista de argumentos `lae` evaluados en el ambiente dado."
([fnc lae amb]
(aplicar (revisar-fnc fnc) (revisar-lae lae) fnc lae amb))
([resu1 resu2 fnc lae amb]
(cond
(error? resu1) (list resu1 amb)
(error? resu2) (list resu2 amb)
(not (seq? fnc)) (list (aplicar-funcion-primitiva fnc lae amb) amb)
:else (aplicar-lambda fnc lae amb))))


(defn aplicar-lambda
"Aplica la funcion lambda `fnc` a `lae` (lista de argumentos evaluados)."
[fnc lae amb]
(cond
(not= (count lae) (count (second fnc))) (list (generar-mensaje-error :wrong-number-args fnc) amb)
(nil? (next (nnext fnc))) (aplicar-lambda-simple fnc lae amb)
:else (aplicar-lambda-multiple fnc lae amb)))


(defn aplicar-lambda-simple
"Evalua un lambda `fnc` con un cuerpo simple"
[fnc lae amb]
(let [lae-con-quotes (map #(if (or (number? %) (string? %) (and (seq? %) (igual? (first %) 'lambda)))
                          %
                          (list 'quote %)) lae),
 nuevos-pares (reduce concat (map list (second fnc) lae-con-quotes)),
 mapa (into (hash-map) (vec (map vec (partition 2 nuevos-pares)))),
 cuerpo (first (nnext fnc)),
 expre (if (and (seq? cuerpo) (seq? (first cuerpo)) (igual? (ffirst cuerpo) 'lambda))
           (cons (first cuerpo) (postwalk-replace mapa (rest cuerpo)))
           (postwalk-replace mapa cuerpo))]
 (evaluar expre amb)))


(defn aplicar-lambda-multiple
"Evalua una funcion lambda `fnc` cuyo cuerpo contiene varias partes."
[fnc lae amb]
(aplicar (cons 'lambda (cons (second fnc) (next (nnext fnc))))
    lae
    (second (aplicar-lambda-simple fnc lae amb))))


(defn aplicar-funcion-primitiva
  "Aplica una funcion primitiva a una `lae` (lista de argumentos evaluados)."
  [fnc lae amb]
    (cond
      (= fnc '+)            (fnc-sumar lae)
      (= fnc '-)            (fnc-restar lae)
      (= fnc '<)            (fnc-menor lae)
      (= fnc '>)            (fnc-mayor lae)
      (= fnc '>=)           (fnc-mayor-o-igual lae)
      

      ;
      ;
      ; Si la funcion primitiva esta identificada por un simbolo, puede determinarse mas rapido que hacer con ella
      ;
      ;
      (igual? fnc 'env) (fnc-env lae amb)
      (igual? fnc 'append)  (fnc-append lae)
      (igual? fnc 'equal?) (fnc-equal? lae)
      (igual? fnc 'read) (fnc-read lae)
      (igual? fnc 'car) (fnc-car lae)
      (igual? fnc 'cdr) (fnc-cdr lae)
      (igual? fnc 'not) (fnc-not lae) 
      (igual? fnc 'cons) (fnc-cons lae) 
      (igual? fnc 'list) (fnc-list lae)
      (igual? fnc 'list?) (fnc-list? lae)
      (igual? fnc 'null?) (fnc-null? lae)
      (igual? fnc 'length) (fnc-length lae)
      (igual? fnc 'display) (fnc-display lae)
      (igual? fnc 'newline) (fnc-newline lae)
      (igual? fnc 'reverse) (fnc-reverse lae)


      ;
      ;
      ; Si la funcion primitiva esta identificada mediante una palabra reservada, debe ignorarse la distincion entre mayusculas y minusculas 
      ;
      ;

      :else 
        (generar-mensaje-error :wrong-type-apply fnc)
    )
)


(defn fnc-car
"Devuelve el primer elemento de una lista."
[lae]
(let [ari (controlar-aridad-fnc lae 1 'car), arg1 (first lae)]
(cond
  (error? ari) ari
  (or (not (seq? arg1)) (empty? arg1)) (generar-mensaje-error :wrong-type-arg1 'car arg1)
  :else (first arg1))))


(defn fnc-cdr
"Devuelve una lista sin su 1ra. posicion."
[lae]
(let [ari (controlar-aridad-fnc lae 1 'cdr), arg1 (first lae)]
(cond
  (error? ari) ari
  (or (not (seq? arg1)) (empty? arg1)) (generar-mensaje-error :wrong-type-arg1 'cdr arg1)
  :else (rest arg1))))


(defn fnc-cons
"Devuelve el resultado de insertar un elemento en la cabeza de una lista."
[lae]
(let [ari (controlar-aridad-fnc lae 2 'cons), arg1 (first lae), arg2 (second lae)]
(cond
  (error? ari) ari
        (not (seq? arg2)) (generar-mensaje-error :only-proper-lists-implemented 'cons)
        :else (cons arg1 arg2))))


(defn fnc-display
"Imprime un elemento por la termina/consola y devuelve #<unspecified>."
[lae]
(let [cant-args (count lae), arg1 (first lae)]
(case cant-args
  1 (do (print arg1) (flush) (symbol "#<unspecified>"))
  2 (generar-mensaje-error :io-ports-not-implemented 'display)
  (generar-mensaje-error :wrong-number-args-prim-proc 'display))))


(defn fnc-env
"Devuelve el ambiente."
[lae amb]
(let [ari (controlar-aridad-fnc lae 0 'env)]
(if (error? ari)
    ari
    amb)))


(defn fnc-length
"Devuelve la longitud de una lista."
[lae]
(let [ari (controlar-aridad-fnc lae 1 'length), arg1 (first lae)]
(cond
  (error? ari) ari
  (not (seq? arg1)) (generar-mensaje-error :wrong-type-arg1 'length arg1)
  :else (count arg1))))


(defn fnc-list
"Devuelve una lista formada por los args."
[lae]
(if (< (count lae) 1)
()
lae))


(defn fnc-list?
"Devuelve #t si un elemento es una lista. Si no, #f."
[lae]
(let [ari (controlar-aridad-fnc lae 1 'list?), arg1 (first lae)]
(if (error? ari)
    ari
    (if (seq? arg1)
        (symbol "#t")
        (symbol "#f")))))


(defn fnc-newline
"Imprime un salto de linea y devuelve #<unspecified>."
[lae]
(let [cant-args (count lae)]
(case cant-args
  0 (do (newline) (flush) (symbol "#<unspecified>"))
  1 (generar-mensaje-error :io-ports-not-implemented 'newline)
  (generar-mensaje-error :wrong-number-args-prim-proc 'newline))))


(defn fnc-not
"Niega el argumento."
[lae]
(let [ari (controlar-aridad-fnc lae 1 'not)]
(if (error? ari)
    ari
    (if (igual? (first lae) (symbol "#f"))
        (symbol "#t")
        (symbol "#f")))))


(defn fnc-null?
"Devuelve #t si un elemento es ()."
[lae]
(let [ari (controlar-aridad-fnc lae 1 'null?)]
(if (error? ari)
    ari
    (if (= (first lae) ())
        (symbol "#t")
        (symbol "#f")))))


(defn fnc-reverse
"Devuelve una lista con los elementos de `lae` en orden inverso."
[lae]
(let [ari (controlar-aridad-fnc lae 1 'reverse), arg1 (first lae)]
(cond
  (error? ari) ari
  (not (seq? arg1)) (generar-mensaje-error :wrong-type-arg1 'reverse arg1)
  :else (reverse arg1))))


(defn controlar-aridad-fnc
"Si la `lae` tiene la longitud esperada, se devuelve este valor (que es la aridad de la funcion).
Si no, devuelve una lista con un mensaje de error."
[lae val-esperado fnc]
(if (= val-esperado (count lae))
val-esperado
(generar-mensaje-error :wrong-number-args-prim-proc fnc)))


(defn imprimir
"Imprime, con salto de linea, atomos o listas en formato estandar (las cadenas
con comillas) y devuelve su valor. Muestra errores sin parentesis."
([elem]
(cond
(= \space elem) elem    ; Si es \space no lo imprime pero si lo devuelve
(and (seq? elem) (starts-with? (apply str elem) ";")) (imprimir elem elem)
:else (do (prn elem) (flush) elem)))
([lis orig]
(cond
(nil? lis) (do (prn) (flush) orig)
:else (do (pr (first lis))
        (print " ")
        (imprimir (next lis) orig)))))


(defn revisar-fnc
"Si la `lis` representa un error lo devuelve; si no, devuelve nil."
[lis] (if (error? lis) lis nil))


(defn revisar-lae
"Si la `lis` contiene alguna sublista que representa un error lo devuelve; si no, devuelve nil."
[lis] (first (remove nil? (map revisar-fnc (filter seq? lis)))))


(defn evaluar-cond
"Evalua una expresion `cond`."
[expre amb]
(if (= (count expre) 1) ; si es el operador solo
(list (generar-mensaje-error :bad-or-missing 'cond expre) amb)
(let [res (drop-while #(and (seq? %) (not (empty? %))) (next expre))]
     (if (empty? res) 
         (evaluar-clausulas-de-cond expre (next expre) amb)
         (list (generar-mensaje-error :bad-or-missing 'cond (first res)) amb)))))


(defn evaluar-clausulas-de-cond
"Evalua las clausulas de cond."
[expre lis amb]
(if (nil? lis)
(list (symbol "#<unspecified>") amb) ; cuando ninguna fue distinta de #f
 (let [res-eval (if (not (igual? (ffirst lis) 'else))
                    (evaluar (ffirst lis) amb)
                    (if (nil? (next lis))
                        (list (symbol "#t") amb)
                        (list (generar-mensaje-error :bad-else-clause 'cond expre) amb)))]
      (cond
        (error? (first res-eval)) res-eval
        (igual? (first res-eval) (symbol "#f")) (recur expre (next lis) (second res-eval)) 
        :else (evaluar-secuencia-en-cond (nfirst lis) (second res-eval))))))


(defn evaluar-secuencia-en-cond
"Evalua secuencialmente las sublistas de `lis`. Devuelve el valor de la ultima evaluacion."
[lis amb]
(if (nil? (next lis))
 (evaluar (first lis) amb)
 (let [res-eval (evaluar (first lis) amb)]
      (if (error? (first res-eval))
           res-eval
          (recur (next lis) (second res-eval))))))


(defn evaluar-eval
"Evalua una expresion `eval`."
[expre amb]
(if (not= (count expre) 2) ; si no son el operador y exactamente 1 argumento
(list (generar-mensaje-error :wrong-number-args (symbol "#<CLOSURE <anon> ...")) amb)
(let [arg (second expre)]
    (if (and (seq? arg) (igual? (first arg) 'quote))
        (evaluar (second arg) amb)
        (evaluar arg amb)))))


(defn evaluar-exit
"Sale del interprete de Scheme."
[expre amb]
(if (> (count expre) 2) ; si son el operador y mas de 1 argumento
(list (generar-mensaje-error :wrong-number-args-prim-proc 'quit) amb)
(list nil nil)))


(defn evaluar-lambda
"Evalua una expresion `lambda`."
[expre amb]
(cond
(< (count expre) 3) ; si son el operador solo o con 1 unico argumento
   (list (generar-mensaje-error :bad-body 'lambda (rest expre)) amb)
(not (seq? (second expre)))
   (list (generar-mensaje-error :bad-params 'lambda expre) amb)
:else (list expre amb)))


(defn evaluar-load
"Evalua una expresion `load`. Carga en el ambiente un archivo `expre` de codigo en Scheme."
[expre amb]
(if (= (count expre) 1) ; si es el operador solo
(list (generar-mensaje-error :wrong-number-args (symbol "#<CLOSURE scm:load ...")) amb)
(list (symbol "#<unspecified>") (cargar-arch amb (second expre)))))


(defn cargar-arch
"Carga y devuelve el contenido de un archivo."
([amb arch]
(let [res (evaluar arch amb),
  nom-original (first res),
  nuevo-amb (second res)]
  (if (error? nom-original)
      (do (imprimir nom-original) nuevo-amb)                 ; Mostrar el error
      (let [nom-a-usar (generar-nombre-arch nom-original)]
            (if (error? nom-a-usar)
                (do (imprimir nom-a-usar) nuevo-amb)          ; Mostrar el error
                (let [tmp (try
                             (slurp nom-a-usar)
                             (catch java.io.FileNotFoundException _
                               (generar-mensaje-error :file-not-found)))]
                     (if (error? tmp)
                         (do (imprimir tmp) nuevo-amb)        ; Mostrar el error
                         (do (spit "scm-temp" (proteger-bool-en-str tmp))
                             (let [ret (with-open [in (java.io.PushbackReader. (reader "scm-temp"))]
                                         (binding [*read-eval* false]
                                           (try
                                             (imprimir (list (symbol ";loading") (symbol nom-original)))
                                             (cargar-arch (second (evaluar (restaurar-bool (read in)) nuevo-amb)) in nom-original nom-a-usar)
                                             (catch Exception e
                                                (imprimir (generar-mensaje-error :end-of-file 'list))))))]
                                   (do (delete-file "scm-temp" true) ret))))))))))
([amb in nom-orig nom-usado]
(try
(cargar-arch (second (evaluar (restaurar-bool (read in)) amb)) in nom-orig nom-usado)
(catch Exception _
(imprimir (list (symbol ";done loading") (symbol nom-usado)))
amb))))


(defn generar-nombre-arch
"Dada una entrada la convierte en un nombre de archivo .scm valido."
[nom]
(if (not (string? nom))
(generar-mensaje-error :wrong-type-arg1 'string-length nom)
(let [n (lower-case nom)]
     (if (nombre-arch-valido? n)
         n
         (str n ".scm")))))    ; Agrega '.scm' al final


(defn nombre-arch-valido?
"Chequea que el string sea un nombre de archivo .scm valido."
[nombre] (and (> (count nombre) 4) (ends-with? nombre ".scm")))


(defn evaluar-quote
"Evalua una expresion `quote`."
[expre amb]
(if (not= (count expre) 2) ; si no son el operador y exactamente 1 argumento
(list (generar-mensaje-error :missing-or-extra 'quote expre) amb)
(list (second expre) amb)))


(defn generar-mensaje-error
"Devuelve un mensaje de error expresado como lista."
([cod]
(case cod 
  :file-not-found (list (symbol ";ERROR:") 'No 'such 'file 'or 'directory)
  :warning-paren (list (symbol ";WARNING:") 'unexpected (symbol "\")\"#<input-port 0>"))
  ()))
([cod fnc]
(cons (symbol ";ERROR:")
   (case cod
  :end-of-file (list (symbol (str fnc ":")) 'end 'of 'file)
  :error (list (symbol (str fnc)))
  :io-ports-not-implemented (list (symbol (str fnc ":")) 'Use 'of 'I/O 'ports 'not 'implemented)
  :only-proper-lists-implemented (list (symbol (str fnc ":")) 'Only 'proper 'lists 'are 'implemented)
  :unbound-variable (list 'unbound (symbol "variable:") fnc)
  :wrong-number-args (list 'Wrong 'number 'of 'args 'given fnc)
  :wrong-number-args-oper (list (symbol (str fnc ":")) 'Wrong 'number 'of 'args 'given)
  :wrong-number-args-prim-proc (list 'Wrong 'number 'of 'args 'given (symbol "#<primitive-procedure") (symbol (str fnc '>)))
  :wrong-type-apply (list 'Wrong 'type 'to 'apply fnc)
  ())))
([cod fnc nom-arg]
(cons (symbol ";ERROR:") (cons (symbol (str fnc ":"))
   (case cod
     :bad-body (list 'bad 'body nom-arg)
     :bad-else-clause (list 'bad 'ELSE 'clause nom-arg)
     :bad-or-missing (list 'bad 'or 'missing 'clauses nom-arg)
     :bad-params (list 'Parameters 'are 'implemented 'only 'as 'lists nom-arg)
     :bad-variable (list 'bad 'variable nom-arg)
     :missing-or-extra (list 'missing 'or 'extra 'expression nom-arg)
     :wrong-type-arg (list 'Wrong 'type 'in 'arg nom-arg)
     :wrong-type-arg1 (list 'Wrong 'type 'in 'arg1 nom-arg)
     :wrong-type-arg2 (list 'Wrong 'type 'in 'arg2 nom-arg)
  ())))))


; FUNCIONES QUE DEBEN SER IMPLEMENTADAS PARA COMPLETAR EL INTERPRETE DE SCHEME (ADEMAS DE COMPLETAR `EVALUAR` Y `APLICAR-FUNCION-PRIMITIVA`):


; Si me llega una cadena con espacios extras, aprovecho re-seq con las expresiones regualres!!
;
; EXPR. REG: #"[^\s]+ "
;     Esta expresion la uso sobre la primera cadena recibida. 
;     Agarra las palabras que arrancan sin espacio, hasta terminar con un primer espacio!
;     >> si la cadena es " Hola,   como   estas?     " me tomará "Hola, como estas? "
;
; "re-seq" con esa exp. me dejará esas palabras en una lista. PERO si la ultima palabra de la cadena
; hubiese terminado con "   ...holaa   ".. en la lista aplicadole dicho "re-seq", su ultimo elemento tendrá un espacio. 
; Entonces, juego un poco con concat, drop-last & last para que ésa ultima palabra de la lista, si hubiese quedado con "holaa "; quiero que me quede "holaa".
;
; user=> (arreglar-espacios-extras "   hola soy       una,               super    cadena           con espacios rebundante           sssss    ")
; "hola soy una, super cadena con espacios rebundante sssss"
;
; user => (quitar-espacios-extras " (   hola soy       una,               super    cadena           con espacios rebundante           sssss    )")
; "(hola soy una, super cadena con espacios rebundante sssss)"

(defn aux-map [word index cantidad-palabras]
  (cond
    ; si indice no es 0 ni 1, y el index es cantidad-palabras y cantidad-palabras -1
    (or 
      (= index 0)
      (= index 1)
      (= index (- cantidad-palabras 1))
      (= index (- cantidad-palabras 2))
    )
      (let [char-word (map char word)]
        (cond 
          (or
            (and (= '\( (first char-word)) (= '\space (second char-word))) ; "( "
            (and (= '\) (first char-word)) (= '\space (second char-word))) ; ") "
          )
            nil 
          (and (= '\( (first char-word)) (not (= '\space (second char-word)))) ; "(palabra"
            (apply str (rest char-word))
          (and                                                                            ; "palabra)"
            (= '\) (last char-word))
            (not (= '\space (nth char-word (- (count char-word) 2))))
          )
            (apply str (butlast char-word))
          (and                           
            (> (count char-word) 2)                                                 ; "palabra) "
            (= '\space (last char-word))
            (= '\) (nth char-word (- (count char-word) 2)))
            (not (= '\space (nth char-word (- (count char-word) 3))))
          )
            (apply str (drop-last 2 char-word))
          :else
            word
        )
      )
    :else 
      word
  )
)

(defn ultima-limpieza [list]
  (drop-last list)
)


(defn quitar-espacios-extras [cadena]
  (let [seq-palabras (re-seq #"[^\s]+ " (str cadena " "))] 
    (str "(" (apply str (ultima-limpieza (filter (comp not nil?) (map aux-map seq-palabras (take (count seq-palabras) (range)) (take (count seq-palabras) (repeat (count seq-palabras))))))) ")")
  )
)

(defn delete-espacio-agregado [cadena]
  (apply str (drop-last (map char cadena)))
)

; LEER-ENTRADA:
; user=> (leer-entrada)
; (hola
; mundo)
; "(hola mundo)"
; user=> (leer-entrada)
; 123
; "123"
(defn leer-entrada
  "Lee una cadena desde la terminal/consola. Si los parentesis no estan correctamente balanceados al presionar Enter/Intro,
  se considera que la cadena ingresada es una subcadena y el ingreso continua. De lo contrario, se la devuelve completa."
  ([]
    (leer-entrada "")
  )
  ([line-old]
    (let [line (read-line)]
      (let [cant-parentesis (verificar-parentesis (str line-old line))]
        (cond 
          (= cant-parentesis 0) 
            (str line-old line)
          (< cant-parentesis 0) 
            (generar-mensaje-error :warning-paren)
          :else
            (leer-entrada (str line-old line " "))
        )
      )
    )
  )
)

(defn remplazar-nil-por-cero [numero]
  (if (nil? numero) 0 numero)
)

; user=> (verificar-parentesis "(hola 'mundo" 1|0
; 1
; user=> (verificar-parentesis "(hola '(mundo)))" 2|3
; -1
; user=> (verificar-parentesis "(hola '(mundo) () 6) 7)" 3|4
; -1
; user=> (verificar-parentesis "(hola '(mundo) () 6) 7) 9)" 4|5
; -1
; user=> (verificar-parentesis "(hola '(mundo) )" 2|2
; 0
(defn verificar-parentesis
"Cuenta los parentesis en una cadena, sumando 1 si `(`, restando 1 si `)`. Si el contador se hace negativo, para y retorna -1."
  [cadena]

  (let [freq_por_chars (frequencies (map str cadena))]
    (let [cant_parentesis_iniciales (remplazar-nil-por-cero (get freq_por_chars "(")),
          cant_parentesis_finales (remplazar-nil-por-cero (get freq_por_chars ")")) ] 
      (cond 
        (> cant_parentesis_iniciales cant_parentesis_finales)
         (- cant_parentesis_iniciales cant_parentesis_finales)
        (< cant_parentesis_iniciales cant_parentesis_finales)
          -1
        :else 
          0
      )
    )
  )
  
)

(defn agregar-new-key-value-si-no-existia-la-key [amb-revisado key-new value-new]
    (let [amb-modificado-vec (vec amb-revisado)]
      (if (not (not-any? (fn [pair-key-value] (= key-new (first pair-key-value))) (partition 2 amb-modificado-vec)))
          (apply list amb-modificado-vec)
          (apply list (conj amb-modificado-vec key-new value-new))

      ) 
    )
)

(defn actualizar-amb-si-existe-la-key [amb key-new value-new]
  (drop-last 
    (map (fn [key_amb value_amb index] (if (and (= key_amb key-new) (odd? index)) value-new value_amb)) 
      (conj amb nil)                              ; '(nil a 1 b 2 c 3)
      (conj (vec amb) nil)                        ;    [a 1 b 2 c 3 nil]
      (take (+ (count amb) 1) (range))            ;    [0 1 2 3 4 5]   .... ésos son los index.. me interesa los indices impares!
    )
  )
)


; user=> (actualizar-amb '(a 1 b 2 c 3) 'd 4)
; (a 1 b 2 c 3 d 4)
; user=> (actualizar-amb '(a 1 b 2 c 3) 'b 4)
; (a 1 b 4 c 3)
; user=> (actualizar-amb '(a 1 b 2 c 3) 'b (list (symbol ";ERROR:") 'mal 'hecho))
; (a 1 b 2 c 3)
; user=> (actualizar-amb () 'b 7)
; (b 7)
    ;; La 1° pasado actualizo la key en caso de que exista en el ambiente
    ;; La 2° pasada reviso si no existia esa key para así agregarlo al final.. terminando el algoritmo como O(2*n). 
        ; Podria mejorar con O(n) si utilizo set! en la 1° pasada, guardando en un true/false si existia o no 
        ; pero busco evitar usar el set! en este paradigma
(defn actualizar-amb
"Devuelve un ambiente actualizado con una clave (nombre de la variable o funcion) y su valor. 
Si el valor es un error, el ambiente no se modifica. De lo contrario, se le carga o reemplaza la nueva informacion."
  [amb key-new value-new]
  (if (error? value-new)
    amb ; si es error, devuelvo como estaba el ambiente
    (let [amb-revisado (actualizar-amb-si-existe-la-key amb key-new value-new)]
      (agregar-new-key-value-si-no-existia-la-key amb-revisado key-new value-new )
    )
  )
)

; user=> (buscar 'c '(a 1 b 2 c 3 d 4 e 5))
; 3
; user=> (buscar 'f '(a 1 b 2 c 3 d 4 e 5))
; (;ERROR: unbound variable: f)
(defn buscar
"Busca una clave en un ambiente (una lista con claves en las posiciones impares [1, 3, 5...] y valores en las pares [2, 4, 6...]
y devuelve el valor asociado. Devuelve un error :unbound-variable si no la encuentra."
  [key-buscada amb]
  (if (empty? amb)
    (generar-mensaje-error :unbound-variable key-buscada)
    (let [valor-buscado (get (apply hash-map amb) key-buscada)]
      (if (nil? valor-buscado)
        (generar-mensaje-error :unbound-variable key-buscada)
        valor-buscado
      )
    )
  )
)

; user=> (error? (list (symbol ";ERROR:") 'mal 'hecho))
; true
; user=> (error? (list 'mal 'hecho))
; false
; user=> (error? (list (symbol ";WARNING:") 'mal 'hecho))
; true
(defn error?
"Devuelve true o false, segun sea o no el arg. una lista con `;ERROR:` o `;WARNING:` como primer elemento."
  [lista_de_error]
  (if (list? lista_de_error)
    (let [primer_elemento (first lista_de_error)]
      (if (or (= primer_elemento (symbol ";ERROR:")) (= primer_elemento (symbol ";WARNING:")))
        true
        false
      )
    )
    false
  )
)

; user=> (proteger-bool-en-str "(or #F #f #t #T)")
; "(or %F %f %t %T)"
; user=> (proteger-bool-en-str "(and (or #F #f #t #T) #T)")
; "(and (or %F %f %t %T) %T)"
; user=> (proteger-bool-en-str "")
; ""
(defn proteger-bool-en-str
"Cambia, en una cadena, #t por %t y #f por %f (y sus respectivas versiones en mayusculas), para poder aplicarle read-string."
  [cadena]
 ; (apply str (replace '{\# \%} (map char cadena))) ; ; 1° Versión! SI funciona.. pero entendí en el foro que podíamos usar "clojure.string/replace"
    (-> cadena
      (clojure.string/replace #"#F" "%F")
      (clojure.string/replace #"#f" "%f")
      (clojure.string/replace #"#T" "%T")
      (clojure.string/replace #"#t" "%t")
    )
)


(def POR_F (symbol "%F"))
(def NUM_F (symbol "#F"))
(def POR_f (symbol "%f"))
(def NUM_f (symbol "#f"))
(def POR_T (symbol "%T"))
(def NUM_T (symbol "#T"))
(def POR_t (symbol "%t"))
(def NUM_t (symbol "#t"))

(defn reemplace-bool [sym]
  (if (symbol? sym)
    (cond 
      (= sym POR_T)
        NUM_T
      (= sym POR_f)
        NUM_f
      (= sym POR_F)
        NUM_F
      (= sym POR_t)
        NUM_t
      :else
         sym
    )
    (if (string? sym)
      (-> sym
        (clojure.string/replace #"%F" "#F")
        (clojure.string/replace #"%f" "#f")
        (clojure.string/replace #"%T" "#T")
        (clojure.string/replace #"%t" "#t")
      )
      sym
    )
  )
)

; Se creó para analizar el uppercase en los IF's.. Ya que el programa (load "demo") en el interprete
; original de Scheme estas funciones devolvian en minusculas:
; > (mapear (lambda (x) (if (equal? x 0) 'Z x)) '(5 0 2 -1 4 6 0 8))
; (5 z 2 -1 4 6 z 8)
; > (SELECCIONAR 5 '(A B C D E F G H I J))
; e
(defn evaluar-quote-upper-case
  [resultado]
  (if (seq? resultado)
    (if (= (first resultado) 'quote)
        (list (first resultado) (read-string (lower-case (str (second resultado)))))
        resultado
    )
    resultado
  )
)

; user=> (restaurar-bool (read-string (proteger-bool-en-str "(and (or #F #f #t #T) #T)")))
; (and (or #F #f #t #T) #T)
; user=> (restaurar-bool (read-string "(and (or %F %f %t %T) %T)") )
; (and (or #F #f #t #T) #T)
(defn restaurar-bool
"Cambia, en un codigo leido con read-string, %t por #t y %f por #f (y sus respectivas versiones en mayusculas)."
  [codigo]
  (let [resultado 
    (if (list? codigo)
        (map (fn [item] 
              (if (not (seq? item))
                (reemplace-bool item)
                (restaurar-bool item)
              )
            )
          codigo
        )
      (reemplace-bool codigo)
    )]
    (evaluar-quote-upper-case resultado)
  )
)


; user=> (igual? 'if 'IF)
; true
; user=> (igual? 'if 'if)
; true
; user=> (igual? 'IF 'IF)
; true
; user=> (igual? 'IF "IF")
; false
; user=> (igual? 6 "6")
; false
; user=> (igual? "if" "IF")
; false
; user=> (igual? '(a (b) c) '(A (B) C))
; true
; user=> (igual? (list "asd" 5) (list "ASD" 5))
; false
; user=> (igual? (list "asd" 5) (list "asd" 5))
; true
(defn igual?
"Verifica la igualdad entre dos elementos al estilo de Scheme (case-insensitive)"
  [x y]
  (cond
    (or (and (string? x) (not (string? y))) (and (string? y) (not (string? x))))
      false 
    (and (string? x) (string? y))
      (= x y)
    (and (list? x) (list? y))
      (reduce (fn [item-a item-b] (and item-a item-b)) (map (fn [item-x item-y] (igual? item-x item-y)) x y))
    :else 
      (let [x-str-lower (lower-case (str x)), y-str-lower (lower-case (str y))]
        (= x-str-lower y-str-lower)
      )
  )
)

'((2 3) (3 ((7))) 5)

; user=> (fnc-append '( (1 2) (3) (4 5) (6 7)))
; (1 2 3 4 5 6 7)
; user=> (fnc-append '( (1 2) 3 (4 5) (6 7)))
; (;ERROR: append: Wrong type in arg 3)
; user=> (fnc-append '( (1 2) A (4 5) (6 7)))
; (;ERROR: append: Wrong type in arg A)
(defn fnc-append
"Devuelve el resultado de fusionar listas."
   [listas]
   (let [not-list (filter (fn [element] (not (seq? element))) listas)]
    (if (empty? not-list) 
      (apply concat listas) 
      (generar-mensaje-error :wrong-type-arg 'append (first not-list))
    )
   )
)


(defn fnc-equal?-aux
  ([x y]
    (igual? x y)
  )
  ([x y & more]
    (if (igual? x y)
      (if (next more)
        (recur y (first more) (next more))
        (igual? y (first more))
      )
      false
    )
  )
)

; user=> (fnc-equal? ())
; #t
; user=> (fnc-equal? '(A))
; #t
; user=> (fnc-equal? '(A a))
; #t
; user=> (fnc-equal? '(A a A))
; #t
; user=> (fnc-equal? '(A a A a))
; #t
; user=> (fnc-equal? '(A a A B))
; #f
; user=> (fnc-equal? '(1 1 1 1))
; #t
; user=> (fnc-equal? '(1 1 2 1))
; #f
(defn fnc-equal?
"Compara elementos. Si son iguales, devuelve #t. Si no, #f."
; VERSIÓN INSPIRADA EN EL "=" DE CLOJURE. PERO EL "=" USADO ES MI "igual?" QUE TIENE EN CUENTA LAS CASES-INSENSITIVE.
; LA VERSIÓN DE CLOJURE SE PUEDE VISUALIZAR EN CLOJURE LLAMANDO ASÍ -> (source =)
  ([lista]
    (if (empty? lista)
        (symbol "#t")
        (if (= (count lista) 1)
          (symbol "#t")
          (if (apply fnc-equal?-aux lista)
            (symbol "#t")
            (symbol "#f")
          )
        )
    )
  )
)

(defn fnc-equal?-aux-new-f
  [x y]
  (cond 
    (and (list? x) (not (list? y)))
      (fnc-equal?)
  )
)

(defn fnc-equal?-f
"Evalua una expresion `or`.  Devuelve una lista con el resultado y un ambiente."
  ([lista]
    (if (empty? lista)
        (symbol "#t")
        (if (= (count lista) 1)
          (symbol "#t")
          (let [resultado (reduce (fn [x y] (fnc-equal?-aux-new-f x y)) lista)]
            (true)  
              (symbol "#t")
            (false)
              (symbol "#f")
            :else
              (symbol "#f")
          )
        )
    )
  )
)





; user=> (fnc-read ())
; (hola
; mundo)
; (hola mundo)
; user=> (fnc-read '(1))
; (;ERROR: read: Use of I/O ports not implemented)
; user=> (fnc-read '(1 2))
; (;ERROR: Wrong number of args given #<primitive-procedure read>)
; user=> (fnc-read '(1 2 3))
; (;ERROR: Wrong number of args given #<primitive-procedure read>)
(defn fnc-read
"Devuelve la lectura de un elemento de Scheme desde la terminal/consola."
;" ATENTO A WITH-IN-STR PARA ANALZIAR STDin!!!!!!!!!!! 
;WITH-OUT-STRING PARA SALIDA ?"
  [lista]
  (cond
    (empty? lista)
      (read)
    (= (count lista) 1)
      (generar-mensaje-error :io-ports-not-implemented 'read)
    :else 
      (generar-mensaje-error :wrong-number-args-prim-proc 'read)
  )
)

; user=> (fnc-sumar ())
; 0
; user=> (fnc-sumar '(3))
; 3
; user=> (fnc-sumar '(3 4))
; 7
; user=> (fnc-sumar '(3 4 5))
; 12
; user=> (fnc-sumar '(3 4 5 6))
; 18
; user=> (fnc-sumar '(A 4 5 6))
; (;ERROR: +: Wrong type in arg1 A)
; user=> (fnc-sumar '(3 A 5 6))
; (;ERROR: +: Wrong type in arg2 A)
; user=> (fnc-sumar '(3 4 A 6))
; (;ERROR: +: Wrong type in arg2 A)
(defn fnc-sumar
  "Suma los elementos de una lista."
  [args]
  (cond 
    (empty? args)
      0
    (not (integer? (first args)))
      (generar-mensaje-error :wrong-type-arg1 '+ (first args))
    (not (every? integer? (rest args)))
      (generar-mensaje-error :wrong-type-arg2 '+ (first (filter (comp not integer?) args)))
    :else 
      (reduce + args)
  )
)

; user=> (fnc-restar ())
; (;ERROR: -: Wrong number of args given)
; user=> (fnc-restar '(3))
; -3
; user=> (fnc-restar '(3 4))
; -1
; user=> (fnc-restar '(3 4 5))
; -6
; user=> (fnc-restar '(3 4 5 6))
; -12
; user=> (fnc-restar '(A 4 5 6))
; (;ERROR: -: Wrong type in arg1 A)
; user=> (fnc-restar '(3 A 5 6))
; (;ERROR: -: Wrong type in arg2 A)
; user=> (fnc-restar '(3 4 A 6))
; (;ERROR: -: Wrong type in arg2 A)
(defn fnc-restar
"Resta los elementos de un lista."
  [args]
  (cond 
    (empty? args)
      (generar-mensaje-error :wrong-number-args-oper '-)
    (and (= (count args) 1) (integer? (first args)))
      (- (first args))
    (not (integer? (first args)))
      (generar-mensaje-error :wrong-type-arg1 '- (first args))
    (not (every? integer? (rest args)))
      (generar-mensaje-error :wrong-type-arg2 '- (first (filter (comp not integer?) args)))
    :else 
      (reduce - args)
  )
)

; Busco el symbolo de la función... 
(defn get-symbol-function [function]
  (first (replace '{_LT_ <, _GT_ >, _GT__EQ_ >=}  (list (symbol (re-find #"(?<=\$).*(?=@)" (str function))))))
)

; La desigualdad es '< or '> or >=
(defn aplicar-desigualdad [desigualdad args]
  (cond 
    (empty? args)
      (symbol "#t")
    :else 
      (try
        (if (apply desigualdad args) 
          (symbol "#t")
          (symbol "#f")
        )
        (catch Exception e
          (if (not (integer? (first args)))
            (generar-mensaje-error :wrong-type-arg1 (get-symbol-function desigualdad) (first args))
            (generar-mensaje-error :wrong-type-arg2 (get-symbol-function desigualdad) (first (filter (comp not integer?) args)))
          )
        )
      )
  )
)

; user=> (fnc-menor ())
; #t
; user=> (fnc-menor '(1))
; #t
; user=> (fnc-menor '(1 2))
; #t
; user=> (fnc-menor '(1 2 3))
; #t
; user=> (fnc-menor '(1 2 3 4))
; #t
; user=> (fnc-menor '(1 2 2 4))
; #f
; user=> (fnc-menor '(1 2 1 4))
; #f
; user=> (fnc-menor '(A 1 2 4))
; (;ERROR: <: Wrong type in arg1 A)
; user=> (fnc-menor '(1 A 1 4))
; (;ERROR: <: Wrong type in arg2 A)
; user=> (fnc-menor '(1 2 A 4))
; (;ERROR: <: Wrong type in arg2 A)
(defn fnc-menor
"Devuelve #t si los numeros de una lista estan en orden estrictamente creciente; si no, #f."
  [args]
  (aplicar-desigualdad < args)
)

; user=> (fnc-mayor ())
; #t
; user=> (fnc-mayor '(1))
; #t
; user=> (fnc-mayor '(2 1))
; #t
; user=> (fnc-mayor '(3 2 1))
; #t
; user=> (fnc-mayor '(4 3 2 1))
; #t
; user=> (fnc-mayor '(4 2 2 1))
; #f
; user=> (fnc-mayor '(4 2 1 4))
; #f
; user=> (fnc-mayor '(A 3 2 1))
; (;ERROR: >: Wrong type in arg1 A)
; user=> (fnc-mayor '(3 A 2 1))
; (;ERROR: >: Wrong type in arg2 A)
; user=> (fnc-mayor '(3 2 A 1))
; (;ERROR: >: Wrong type in arg2 A)
(defn fnc-mayor
"Devuelve #t si los numeros de una lista estan en orden estrictamente decreciente; si no, #f."
  [args]
  (aplicar-desigualdad > args)
)

; user=> (fnc-mayor-o-igual ())
; #t
; user=> (fnc-mayor-o-igual '(1))
; #t
; user=> (fnc-mayor-o-igual '(2 1))
; #t
; user=> (fnc-mayor-o-igual '(3 2 1))
; #t
; user=> (fnc-mayor-o-igual '(4 3 2 1))
; #t
; user=> (fnc-mayor-o-igual '(4 2 2 1))
; #t
; user=> (fnc-mayor-o-igual '(4 2 1 4))
; #f
; user=> (fnc-mayor-o-igual '(A 3 2 1))
; (;ERROR: >=: Wrong type in arg1 A)
; user=> (fnc-mayor-o-igual '(3 A 2 1))
; (;ERROR: >=: Wrong type in arg2 A)
; user=> (fnc-mayor-o-igual '(3 2 A 1))
; (;ERROR: >=: Wrong type in arg2 A)
(defn fnc-mayor-o-igual
"Devuelve #t si los numeros de una lista estan en orden decreciente; si no, #f."
  [args]
  (aplicar-desigualdad >= args)
)

; user=> (evaluar-escalar 32 '(x 6 y 11 z "hola"))
; (32 (x 6 y 11 z "hola"))
; user=> (evaluar-escalar "chau" '(x 6 y 11 z "hola"))
; ("chau" (x 6 y 11 z "hola"))
; user=> (evaluar-escalar 'y '(x 6 y 11 z "hola"))
; (11 (x 6 y 11 z "hola"))
; user=> (evaluar-escalar 'z '(x 6 y 11 z "hola"))
; ("hola" (x 6 y 11 z "hola"))
; user=> (evaluar-escalar 'n '(x 6 y 11 z "hola"))
; ((;ERROR: unbound variable: n) (x 6 y 11 z "hola"))
(defn evaluar-escalar
"Evalua una expresion escalar. Devuelve una lista con el resultado y un ambiente."
  [escalar amb]
  (if (not (symbol? escalar))
    (list escalar amb)
    (let [amb-map (partition 2 amb)]
      (let [pair (filter (fn [x] (= (str (first x)) (lower-case (str escalar)))) amb-map)]
        (if (empty? pair)
          (list (generar-mensaje-error :unbound-variable escalar) amb)
          (list (second (first pair)) amb)
        )
      )
    )
  )
)

; user=> (evaluar-define '(define x 2) '(x 1))
; (#<unspecified> (x 2))
; user=> (evaluar-define '(define (f x) (+ x 1)) '(x 1))
; (#<unspecified> (x 1 f (lambda (x) (+ x 1))))
; user=> (evaluar-define '(define) '(x 1))
; ((;ERROR: define: missing or extra expression (define)) (x 1))
; user=> (evaluar-define '(define x) '(x 1))
; ((;ERROR: define: missing or extra expression (define x)) (x 1))
; user=> (evaluar-define '(define x 2 3) '(x 1))
; ((;ERROR: define: missing or extra expression (define x 2 3)) (x 1))
; user=> (evaluar-define '(define ()) '(x 1))
; ((;ERROR: define: missing or extra expression (define ())) (x 1))
; user=> (evaluar-define '(define () 2) '(x 1))
; ((;ERROR: define: bad variable (define () 2)) (x 1))
; user=> (evaluar-define '(define 2 x) '(x 1))
; ((;ERROR: define: bad variable (define 2 x)) (x 1))
(defn evaluar-define
"Evalua una expresion `define`. Devuelve una lista con el resultado y un ambiente actualizado con la definicion."
  [exp amb]
  (if (or (and (not (= (count exp) 3)) (not (seq? (second exp)))) (< (count exp) 3))
    (list (generar-mensaje-error :missing-or-extra 'define exp) amb)
    (cond
      (and (not (symbol? (second exp))) (or (number? (second exp)) (empty? (second exp))))
        (list (generar-mensaje-error :bad-variable 'define exp) amb)
      (and (seq? (nth exp 1)) (seq? (nth exp 2)) (> (count exp) 2)) ; lambda!
          (list (symbol "#<unspecified>") (actualizar-amb amb (read-string (lower-case (str (first (second exp))))) (cons 'lambda (cons (rest (second exp)) (drop 2 exp)))))
      :else 
        (let [amb-updated (actualizar-amb amb (read-string (lower-case (str (nth exp 1)))) (first (evaluar (nth exp 2) amb)))]
            (list (symbol "#<unspecified>") amb-updated)
        )
    )
  )
)

; user=> (evaluar-if '(if 1 2) '(n 7))
; (2 (n 7))
; user=> (evaluar-if '(if 1 n) '(n 7))
; (7 (n 7))
; user=> (evaluar-if '(if 1 n 8) '(n 7))
; (7 (n 7))
; user=> (evaluar-if (list 'if (symbol "#f") 'n) (list 'n 7 (symbol "#f") (symbol "#f")))
; (#<unspecified> (n 7 #f #f))
; user=> (evaluar-if (list 'if (symbol "#f") 'n 8) (list 'n 7 (symbol "#f") (symbol "#f")))
; (8 (n 7 #f #f))
; user=> (evaluar-if (list 'if (symbol "#f") 'n '(set! n 9)) (list 'n 7 (symbol "#f") (symbol "#f")))
; (#<unspecified> (n 9 #f #f))
; user=> (evaluar-if '(if) '(n 7))
; ((;ERROR: if: missing or extra expression (if)) (n 7))
; user=> (evaluar-if '(if 1) '(n 7))
; ((;ERROR: if: missing or extra expression (if 1)) (n 7))
(defn evaluar-if
"Evalua una expresion `if`. Devuelve una lista con el resultado y un ambiente eventualmente modificado."
  [exp amb]
  (if (or (> (count exp) 4) (< (count exp) 3))
    (list (generar-mensaje-error :missing-or-extra 'if exp) amb)
    (let [condicion-evaluada (first (evaluar (nth exp 1) amb))]
      (if (or (= condicion-evaluada (symbol "#t")) (= condicion-evaluada 1))
        (evaluar (evaluar-quote-upper-case (nth exp 2)) amb)
        (if (= (count exp) 3)
          (list (symbol "#<unspecified>") amb)
          (evaluar (evaluar-quote-upper-case (nth exp 3)) amb)
        )
      )
    )
  )
)

(defn scm-bool-to-clojure-bool
    [boolean]
    (cond
      (or (= boolean (symbol "#t")) (= boolean (symbol "#T")))
        true
      (or (= boolean (symbol "#f")) (= boolean (symbol "#F")))
        false
      :else
        boolean
    )
  )


(defn clojure-bool-to-scm-bool
  [boolean]
  (cond
    (= boolean true)
      (symbol "#t")
    (= boolean false)
      (symbol "#f")
    :else
      boolean
  )
)

(defn or-aux
  [x y amb]
    (let [primero (first (evaluar x amb)),
          segundo (first (evaluar y amb))]
          (clojure-bool-to-scm-bool (or (scm-bool-to-clojure-bool primero) (scm-bool-to-clojure-bool segundo)))
    )
)

; user=> (evaluar-or (list 'or) (list (symbol "#f") (symbol "#f") (symbol "#t") (symbol "#t")))
; (#f (#f #f #t #t))
; user=> (evaluar-or (list 'or (symbol "#t")) (list (symbol "#f") (symbol "#f") (symbol "#t") (symbol "#t")))
; (#t (#f #f #t #t))
; user=> (evaluar-or (list 'or 7) (list (symbol "#f") (symbol "#f") (symbol "#t") (symbol "#t")))
; (7 (#f #f #t #t))
; user=> (evaluar-or (list 'or (symbol "#f") 5) (list (symbol "#f") (symbol "#f") (symbol "#t") (symbol "#t")))
; (5 (#f #f #t #t))
; user=> (evaluar-or (list 'or (symbol "#f")) (list (symbol "#f") (symbol "#f") (symbol "#t") (symbol "#t")))
; (#f (#f #f #t #t))
(defn evaluar-or
"Evalua una expresion `or`.  Devuelve una lista con el resultado y un ambiente."
  [exp amb]
  (cond
    (= (count exp) 1)
      (list (symbol "#f") amb)
    (= (count exp) 2)
      (evaluar (nth exp 1) amb)
    (= (count exp) 3)
      (list (or-aux (nth exp 1) (nth exp 2) amb) amb)
    :else 
      (list (reduce (fn [x y] (or-aux x y amb)) (pop exp)) amb)
  )
)

; user=> (evaluar-set! '(set! x 1) '(x 0))
; (#<unspecified> (x 1))
; user=> (evaluar-set! '(set! x 1) '())
; ((;ERROR: unbound variable: x) ())
; user=> (evaluar-set! '(set! x) '(x 0))
; ((;ERROR: set!: missing or extra expression (set! x)) (x 0))
; user=> (evaluar-set! '(set! x 1 2) '(x 0))
; ((;ERROR: set!: missing or extra expression (set! x 1 2)) (x 0))
; user=> (evaluar-set! '(set! 1 2) '(x 0))
; ((;ERROR: set!: bad variable 1) (x 0))
(defn evaluar-set!
"Evalua una expresion `set!`. Devuelve una lista con el resultado y un ambiente actualizado con la redefinicion."
  [exp amb]
  (if (not (= (count exp) 3))
    (list (generar-mensaje-error :missing-or-extra 'set! exp) amb)
    (cond
      (and (not (symbol? (second exp))) (or (number? (second exp)) (empty? (second exp))))
        (list (generar-mensaje-error :bad-variable 'set! (nth exp 1)) amb)
      :else 
        (if (empty? amb)
          (list (generar-mensaje-error :unbound-variable (nth exp 1)) amb)
          (let [encontrado (buscar (nth exp 1) amb)]
              (if (error? encontrado)
                (list encontrado amb)
                (list (symbol "#<unspecified>") (actualizar-amb amb (read-string (lower-case (str (nth exp 1)))) (first (evaluar (nth exp 2) amb))))
              )
          )
        )
    )
  )
)

; Al terminar de cargar el archivo en el REPL de Clojure, se debe devolver true.

true