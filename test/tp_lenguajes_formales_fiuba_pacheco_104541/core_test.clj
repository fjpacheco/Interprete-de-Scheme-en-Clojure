(ns tp-lenguajes-formales-fiuba-pacheco-104541.core-test)

(require '[clojure.test :refer :all]
         '[tp-lenguajes-formales-fiuba-pacheco-104541.scheme
            :refer [
                      generar-mensaje-error 
                      fnc-sumar 
                      fnc-restar
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