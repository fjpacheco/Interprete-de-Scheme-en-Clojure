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