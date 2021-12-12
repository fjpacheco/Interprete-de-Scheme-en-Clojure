(ns tp-lenguajes-formales-fiuba-pacheco-104541.core-test)

(require '[clojure.test :refer :all]
         '[tp-lenguajes-formales-fiuba-pacheco-104541.scheme
            :refer [
                      generar-mensaje-error 
                      fnc-sumar 
                      fnc-restar
                      fnc-menor
                      fnc-mayor
                      fnc-mayor-o-igual
                      verificar-parentesis
                      leer-entrada
                      actualizar-amb
                      error?
                      buscar
                      proteger-bool-en-str
                      restaurar-bool
                      igual?
                      fnc-append
                      fnc-equal?
                      fnc-read
                      evaluar-escalar
                      evaluar-define
                      evaluar-set!
                   ]
          ]
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
(deftest fnc-sumar-test
  (testing "Prueba de la funcion: fnc-sumar"
    (is (= 0 (fnc-sumar ())))
    (is (= 3 (fnc-sumar '(3))))
    (is (= 7 (fnc-sumar '(3 4))))
    (is (= 12 (fnc-sumar '(3 4 5))))
    (is (= 18 (fnc-sumar '(3 4 5 6))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '+ 'A) (fnc-sumar '(A 4 5 6))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '+ 'A) (fnc-sumar '(3 A 5 6))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '+ 'A) (fnc-sumar '(3 4 A 6))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '+ 'A) (fnc-sumar '(3 4 A B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '+ '(2 3 4)) (fnc-sumar '(1 (2 3 4)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '+ '(3 4 5)) (fnc-sumar '(1 2 (3 4 5)))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '+ 'A) (fnc-sumar '(A))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '+ 'A) (fnc-sumar '(A B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '+ 'B) (fnc-sumar '(2 B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '+ ()) (fnc-sumar '(2 ()))))

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
(deftest fnc-restar-test
  (testing "Prueba de la funcion: fnc-restar"
    (is (= (generar-mensaje-error :wrong-number-args-oper '-) (fnc-restar ())))
    (is (= -3 (fnc-restar '(3))))
    (is (= -1 (fnc-restar '(3 4))))
    (is (= -6 (fnc-restar '(3 4 5))))
    (is (= -12 (fnc-restar '(3 4 5 6))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '- 'A) (fnc-restar '(A 4 5 6))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '- 'A) (fnc-restar '(3 A 5 6))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '- 'A) (fnc-restar '(3 4 A 6))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '- 'A) (fnc-restar '(3 4 A B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '- '(2 3 4)) (fnc-restar '(1 (2 3 4)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '- '(3 4 5)) (fnc-restar '(1 2 (3 4 5)))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '- 'A) (fnc-restar '(A))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '- 'A) (fnc-restar '(A B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '- 'B) (fnc-restar '(2 B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '- ()) (fnc-restar '(2 ()))))
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
(deftest fnc-menor-test
  (testing "Prueba de la funcion: fnc-menor"
    (is (= (symbol "#t") (fnc-menor ())))
    (is (= (symbol "#t") (fnc-menor '(1))))
    (is (= (symbol "#t") (fnc-menor '(1 2))))
    (is (= (symbol "#t") (fnc-menor '(1 2 3))))
    (is (= (symbol "#t") (fnc-menor '(1 2 3 4))))
    (is (= (symbol "#f") (fnc-menor '(1 2 2 4))))
    (is (= (symbol "#f") (fnc-menor '(1 2 1 4))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '< 'A) (fnc-menor '(A 1 2 4))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '< 'A) (fnc-menor '(1 A 1 4))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '< 'A) (fnc-menor '(1 2 A 4))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '< 'B) (fnc-menor '(1 2 B 4 A))))
    (is (= (symbol "#f") (fnc-menor '(1 2 2 4 A B C D E))))
    (is (= (symbol "#f") (fnc-menor '(1 2 1 4 HOLAAA))))
    (is (= (symbol "#f") (fnc-menor '(1 2 1 4 '(1 2 3)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '< '(2 3 4)) (fnc-menor '(1 (2 3 4)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '< '(3 4 5)) (fnc-menor '(1 2 (3 4 5)))))
    (is (= (symbol "#t") (fnc-menor '(A))))
    (is (= (symbol "#t") (fnc-menor '((1 0 2)))))
    (is (= (symbol "#t") (fnc-menor '((1 2 3)))))
    (is (= (symbol "#t") (fnc-menor '((2 2 2)))))
    (is (= (symbol "#t") (fnc-menor '((2 1 0)))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '< 'A) (fnc-menor '(A B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '< 'B) (fnc-menor '(2 B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '< ()) (fnc-menor '(2 ()))))
  )
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
(deftest fnc-mayor-test
  (testing "Prueba de la funcion: fnc-mayor"
    (is (= (symbol "#t") (fnc-mayor ())))
    (is (= (symbol "#t") (fnc-mayor '(1))))
    (is (= (symbol "#t") (fnc-mayor '(2 1))))
    (is (= (symbol "#t") (fnc-mayor '(3 2 1))))
    (is (= (symbol "#t") (fnc-mayor '(4 3 2 1))))
    (is (= (symbol "#f") (fnc-mayor '(4 2 2 1))))
    (is (= (symbol "#f") (fnc-mayor '(4 2 1 4))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '> 'A) (fnc-mayor '(A 3 2 1))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '> 'A) (fnc-mayor '(3 A 2 1))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '> 'A) (fnc-mayor '(3 2 A 1))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '> '(2 3 4)) (fnc-mayor '(1 (2 3 4)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '> '(3 4 5)) (fnc-mayor '(2 1 (3 4 5)))))
    (is (= (symbol "#f") (fnc-mayor '(4 2 1 4 A))))
    (is (= (symbol "#f") (fnc-mayor '(1 2 2 4 A B C D E))))
    (is (= (symbol "#f") (fnc-mayor '(1 2 1 4 HOLAAA))))
    (is (= (symbol "#f") (fnc-mayor '(1 2 1 4 '(1 2 3)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '> '(2 3 4)) (fnc-mayor '(1 (2 3 4)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '> '(3 4 5)) (fnc-mayor '(2 1 (3 4 5)))))
    (is (= (symbol "#t") (fnc-mayor '(A))))
    (is (= (symbol "#t") (fnc-mayor '((1 0 2)))))
    (is (= (symbol "#t") (fnc-mayor '((1 2 3)))))
    (is (= (symbol "#t") (fnc-mayor '((2 2 2)))))
    (is (= (symbol "#t") (fnc-mayor '((2 1 0)))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '> 'A) (fnc-mayor '(A B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '> 'B) (fnc-mayor '(2 B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '> ()) (fnc-mayor '(2 ()))))
  )
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
(deftest fnc-mayor-o-igual-test
  (testing "Prueba de la funcion: fnc-mayor-o-igual"
    (is (= (symbol "#t") (fnc-mayor-o-igual ())))
    (is (= (symbol "#t") (fnc-mayor-o-igual '(1))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '(2 1))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '(3 2 1))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '(4 3 2 1))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '(4 2 2 1))))
    (is (= (symbol "#f") (fnc-mayor-o-igual '(4 2 1 4))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '>= 'A) (fnc-mayor-o-igual '(A 3 2 1))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '>= 'A) (fnc-mayor-o-igual '(3 A 2 1))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '>= 'A) (fnc-mayor-o-igual '(3 2 A 1))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '>= '(2 3 4)) (fnc-mayor-o-igual '(1 (2 3 4)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '>= '(3 4 5)) (fnc-mayor-o-igual '(2 1 (3 4 5)))))
    (is (= (symbol "#f") (fnc-mayor-o-igual '(4 2 1 4 A))))
    (is (= (symbol "#f") (fnc-mayor-o-igual '(1 2 2 4 A B C D E))))
    (is (= (symbol "#f") (fnc-mayor-o-igual '(1 2 1 4 HOLAAA))))
    (is (= (symbol "#f") (fnc-mayor-o-igual '(1 2 1 4 '(1 2 3)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '>= '(2 3 4)) (fnc-mayor-o-igual '(1 (2 3 4)))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '>= '(3 4 5)) (fnc-mayor-o-igual '(2 1 (3 4 5)))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '(A))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '((1 0 2)))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '((1 2 3)))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '((2 2 2 2)))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '((2 2 2 1)))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '(2 2 2))))
    (is (= (symbol "#t") (fnc-mayor-o-igual '((2 1 0)))))
    (is (= (generar-mensaje-error :wrong-type-arg1 '>= 'A) (fnc-mayor-o-igual '(A B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '>= 'B) (fnc-mayor-o-igual '(2 B))))
    (is (= (generar-mensaje-error :wrong-type-arg2 '>= ()) (fnc-mayor-o-igual '(2 ()))))
  )
)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;,,,


; user=> (verificar-parentesis "(hola 'mundo")
; 1
; user=> (verificar-parentesis "(hola '(mundo)))")
; -1
; user=> (verificar-parentesis "(hola '(mundo) () 6) 7)")
; -1
; user=> (verificar-parentesis "(hola '(mundo) () 6) 7) 9)")
; -1
; user=> (verificar-parentesis "(hola '(mundo) )")
; 0
(deftest fnc-verificar-parentesis-test
  (testing "Prueba de la funcion: verificar-parentesis"
    (is (=  1 (verificar-parentesis "(hola 'mundo")))
    (is (= -1 (verificar-parentesis "(hola '(mundo)))") ))
    (is (= -1 (verificar-parentesis "(hola '(mundo) () 6) 7)") ))
    (is (= -1 (verificar-parentesis "(hola '(mundo) () 6) 7) 9)") ))
    (is (=  0 (verificar-parentesis "(hola '(mundo) )") ))
  )
)

; user=> (leer-entrada)
; (hola
; mundo)
; "(hola mundo)"
; user=> (leer-entrada)
; 123
; "123"
(deftest fnc-leer-entrada-test
  (testing "Prueba de la funcion: leer-entrada"
    (is (= "(hola mundo)" (with-in-str "(hola\nmundo)" (leer-entrada))))
    (is (= "123" (with-in-str "123" (leer-entrada)))) ; "123" no es númerico! Es string.. 
                                                      ; Pero si se ejecuta en clojure (leer-entrada) 
                                                      ; con el numero 123, te devuelve "123"
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
(deftest fnc-actualizar-amb-test
  (testing "Prueba de la funcion: actualizar-amb"
    (is (= '(a 1 b 2 c 3 d 4) (actualizar-amb '(a 1 b 2 c 3) 'd 4)))
    (is (= '(a 1 b 4 c 3) (actualizar-amb '(a 1 b 2 c 3) 'b 4)))
    (is (= '(a 1 b 2 c 3) (actualizar-amb '(a 1 b 2 c 3) 'b (list (symbol ";ERROR:") 'mal 'hecho))))
    (is (= '(b 7) (actualizar-amb () 'b 7)))
  )
) 

; user=> (error? (list (symbol ";ERROR:") 'mal 'hecho))
; true
; user=> (error? (list 'mal 'hecho))
; false
; user=> (error? (list (symbol ";WARNING:") 'mal 'hecho))
; true
(deftest fnc-error-test
  (testing "Prueba de la funcion: error?"
    (is (= true (error? (list (symbol ";ERROR:") 'mal 'hecho))))
    (is (= false (error? (list 'mal 'hecho))))
    (is (= true (error? (list (symbol ";WARNING:") 'mal 'hecho))))
  )
)
 

; user=> (buscar 'c '(a 1 b 2 c 3 d 4 e 5))
; 3
; user=> (buscar 'f '(a 1 b 2 c 3 d 4 e 5))
; (;ERROR: unbound variable: f)
(deftest fnc-buscar-test
  (testing "Prueba de la funcion: buscar"
    (is (= 3 (buscar 'c '(a 1 b 2 c 3 d 4 e 5))))
    (is (= (generar-mensaje-error :unbound-variable 'f (buscar 'f '(a 1 b 2 c 3 d 4 e 5)))))
  )
)

; user=> (proteger-bool-en-str "(or #F #f #t #T)")
; "(or %F %f %t %T)"
; user=> (proteger-bool-en-str "(and (or #F #f #t #T) #T)")
; "(and (or %F %f %t %T) %T)"
; user=> (proteger-bool-en-str "")
; ""
(deftest fnc-proteger-bool-en-str-test
  (testing "Prueba de la funcion: proteger-bool-en-str"
    (is (= "(or %F %f %t %T)" (proteger-bool-en-str "(or #F #f #t #T)")))
    (is (= "(1 2 3 4)" (proteger-bool-en-str "(1 2 3 4)")))
    (is (= "(and (or %F %f %t %T) %T)" (proteger-bool-en-str "(and (or #F #f #t #T) #T)")))
    (is (= "1" (proteger-bool-en-str "1")))
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

; user=> (restaurar-bool (read-string (proteger-bool-en-str "(and (or #F #f #t #T) #T)")))
; (and (or #F #f #t #T) #T)
; user=> (restaurar-bool (read-string "(and (or %F %f %t %T) %T)"))
; (and (or #F #f #t #T) #T)
(deftest fnc-restaurar-bool
  (testing "Prueba de la funcion: restaurar-bool"
    (is (= (conj (conj (conj () NUM_T) (conj (conj (conj (conj (conj () NUM_T) NUM_t) NUM_f) NUM_F) 'or)) 'and) (restaurar-bool (read-string (proteger-bool-en-str "(and (or #F #f #t #T) #T)")))))
    (is (= (conj (conj (conj () NUM_T) (conj (conj (conj (conj (conj () NUM_T) NUM_t) NUM_f) NUM_F) 'or)) 'and) (restaurar-bool (read-string "(and (or %F %f %t %T) %T)"))))
    (is (= 1 (restaurar-bool (read-string "1"))))
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
; user=> (igual? '(list 'if 5) '(list 'IF 5))
; true
(deftest fnc-igual?
  (testing "Prueba de la funcion: igual?"
    (is (= true (igual? 'if 'IF)))
    (is (= true (igual? 'if 'if)))
    (is (= true (igual? 'IF 'IF)))
    (is (= false (igual? 'IF "IF")))
    (is (= false (igual? 6 "6")))
    (is (= false (igual? "if" "IF")))
    (is (= true (igual? '(a (b) c) '(A (B) C))))
    (is (= false (igual? (list "asd" 5) (list "ASD" 5))))
    (is (= false (igual? '(list "asd" 5) '(list "ASD" 5))))
    (is (= true (igual? (list "asd" 5) (list "asd" 5))))
    (is (= true (igual? '(list 'if 5) '(list 'IF 5))))
    (is (= true (igual? (list 'if 5) (list 'IF 5))))
  )
)

; user=> (fnc-append '( (1 2) (3) (4 5) (6 7)))
; (1 2 3 4 5 6 7)
; user=> (fnc-append '( (1 2) 3 (4 5) (6 7)))
; (;ERROR: append: Wrong type in arg 3)
; user=> (fnc-append '( (1 2) A (4 5) (6 7)))
; (;ERROR: append: Wrong type in arg A)
(deftest fnc-append-test
  (testing "Prueba de la funcion: append"
    (is (= '(1 2 3 4 5 6 7) (fnc-append '( (1 2) (3) (4 5) (6 7)))))
    (is (= (generar-mensaje-error :wrong-type-arg 'append 3) (fnc-append '( (1 2) 3 (4 5) (6 7)))))
    (is (= (generar-mensaje-error :wrong-type-arg 'append 'A) (fnc-append '( (1 2) A (4 5) (6 7)))))
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
; user=> (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13))))
; #t 
; #t (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13))))
; #f (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '15 '13) (list '12 '13) (list '12 '13))))
; #f (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '11 '13) (list '12 '13) (list '12 '13))))
; #f (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '11 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13))))
; #f (fnc-equal? (list (list (list '11 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13))))
; #t (fnc-equal? '(0 3) '(0 3))
; #t (fnc-equal? '(0 0) '(0 0))
; #f (fnc-equal? '(0 1) '(0 0))
; #t (fnc-equal? '(0 3 1 2) '(0 3 1 2))
; #t (fnc-equal? 1 1)
; #f (fnc-equal? 0 1)
; #f (fnc-equal? 1 0)
; #t (fnc-equal? '(0 3) '(0 3) '(0 3))
; #f (fnc-equal? '(0 1) '(0 3) '(0 3))
; #f (fnc-equal? '(0 3) '(2 3) '(0 3))
; #t (fnc-equal? '(0 0) '(0 0) '(0 0) '(0 0))
; #f (fnc-equal? '(0 0) '(0 1) '(0 0) '(0 0) '(0 0) '(0 0) )
; #f (fnc-equal? '(0 0) '(0 0) '(0 0) '(0 0) '(0 0) '(0 1) )
; #f (fnc-equal? 8 5 5 5 5 5 5 5 5 5 5 5 5 )
; #f (fnc-equal? 1 1 1 1 1 2 1 1 1 1 1 )
(deftest fnc-equal?-test
  (testing "Prueba de la funcion: equal?"
    ;(is (= (symbol "#t") (fnc-equal? '("A" "a" "A" "A")))) // TODO!!! Proclive a problemas....
    (is (= (symbol "#t") (fnc-equal? ())))
    (is (= (symbol "#t") (fnc-equal? '(A))))
    (is (= (symbol "#t") (fnc-equal? '(A a))))
    (is (= (symbol "#t") (fnc-equal? '(A a A))))
    (is (= (symbol "#t") (fnc-equal? '(A a A a))))
    (is (= (symbol "#f") (fnc-equal? '(A a A B))))
    (is (= (symbol "#t") (fnc-equal? '(1 1 1 1))))
    (is (= (symbol "#f") (fnc-equal? '(1 1 2 1))))   
    (is (= (symbol "#t") (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13))))))
    (is (= (symbol "#t") (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13))))))
    (is (= (symbol "#f") (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '15 '13) (list '12 '13) (list '12 '13))))))  
    (is (= (symbol "#f") (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '11 '13) (list '12 '13) (list '12 '13))))))  
    (is (= (symbol "#f") (fnc-equal? (list (list (list '12 '13) (list '12 '13) (list '12 '13)) (list (list '11 '13) (list '12 '13) (list '12 '13)) (list (list '12 '13) (list '12 '13) (list '12 '13))))))  
    (is (= (symbol "#f") (fnc-equal? '(A a A B))))
    (is (= (symbol "#f") (fnc-equal? '("(1 2)" (1 2)))))
    (is (= (symbol "#f") (fnc-equal? '("(1 2)" '(1 2)))))
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
(deftest fnc-read-test
  (testing "Prueba de la funcion: read"
    (is (= (list (symbol "hola") (symbol "mundo")) (with-in-str "(hola\nmundo)" (fnc-read ()))))
    (is (= (generar-mensaje-error :io-ports-not-implemented 'read) (fnc-read '(1))))
    (is (= (generar-mensaje-error :wrong-number-args-prim-proc 'read) (fnc-read '(1 2))))
    (is (= (generar-mensaje-error :wrong-number-args-prim-proc 'read) (fnc-read '(1 2 3))))
  )
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
(deftest evaluar-escalar-test
  (testing "Prueba de la funcion: evaluar-escalar"
    (is (= (list 32 (list (symbol "x") 6 (symbol "y") 11 (symbol "z") "hola")) (evaluar-escalar 32 '(x 6 y 11 z "hola"))))
    (is (= (list "chau" (list (symbol "x") 6 (symbol "y") 11 (symbol "z") "hola")) (evaluar-escalar "chau" '(x 6 y 11 z "hola"))))
    (is (= (list 11 (list (symbol "x") 6 (symbol "y") 11 (symbol "z") "hola")) (evaluar-escalar 'y '(x 6 y 11 z "hola"))))
    (is (= (list "hola" (list (symbol "x") 6 (symbol "y") 11 (symbol "z") "hola")) (evaluar-escalar 'z '(x 6 y 11 z "hola"))))
    (is (= (list (generar-mensaje-error :unbound-variable 'n) (list (symbol "x") 6 (symbol "y") 11 (symbol "z") "hola")) (evaluar-escalar 'n '(x 6 y 11 z "hola"))))
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
(deftest evaluar-define-test
  (testing "Prueba de la funcion: evaluar-define"
    (is (= (list (symbol "#<unspecified>") (list (symbol "x") 2)) (evaluar-define '(define x 2) '(x 1))))
    (is (= (list (symbol "#<unspecified>") (list (symbol "x") 1 (symbol "y") 2)) (evaluar-define '(define y 2) '(x 1))))
    (is (= (list (symbol "#<unspecified>") (list (symbol "x") 1 (symbol "f") '(lambda (x) (+ x 1)))) (evaluar-define '(define (f x) (+ x 1)) '(x 1))))
    (is (= (list (generar-mensaje-error :missing-or-extra 'define '(define)) (list (symbol "x") 1)) (evaluar-define '(define) '(x 1))))
    (is (= (list (generar-mensaje-error :missing-or-extra 'define '(define x)) (list (symbol "x") 1)) (evaluar-define '(define x) '(x 1))))
    (is (= (list (generar-mensaje-error :missing-or-extra 'define '(define x 2 3)) (list (symbol "x") 1)) (evaluar-define '(define x 2 3) '(x 1))))
    (is (= (list (generar-mensaje-error :missing-or-extra 'define '(define ())) (list (symbol "x") 1)) (evaluar-define '(define ()) '(x 1))))
    (is (= (list (generar-mensaje-error :bad-variable 'define '(define () 2)) (list (symbol "x") 1)) (evaluar-define '(define () 2) '(x 1))))
    (is (= (list (generar-mensaje-error :bad-variable 'define '(define 2 x)) (list (symbol "x") 1)) (evaluar-define '(define 2 x) '(x 1))))
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
(deftest evaluar-set!-test
  (testing "Prueba de la funcion: evaluar-set!"
    (is (= (list (symbol "#<unspecified>") (list (symbol "x") 1)) (evaluar-set! '(set! x 1) '(x 0))))
    (is (= (list (generar-mensaje-error :unbound-variable 'x) ()) (evaluar-set! '(set! x 1) '())))
    (is (= (list (generar-mensaje-error :missing-or-extra 'set! '(set! x)) (list (symbol "x") 0)) (evaluar-set! '(set! x) '(x 0))))
    (is (= (list (generar-mensaje-error :missing-or-extra 'set! '(set! x 1 2)) (list (symbol "x") 0)) (evaluar-set! '(set! x 1 2) '(x 0))))
    (is (= (list (generar-mensaje-error :bad-variable 'set! 1) (list (symbol "x") 0)) (evaluar-set! '(set! 1 2) '(x 0))))
  )
)