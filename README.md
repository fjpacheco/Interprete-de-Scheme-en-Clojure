[![Clojure CI](https://github.com/fjpacheco/Interprete-de-Scheme-en-Clojure/actions/workflows/clojure.yml/badge.svg?branch=master)](https://github.com/fjpacheco/Interprete-de-Scheme-en-Clojure/actions/workflows/clojure.yml)
[![Materia e Institución](https://img.shields.io/badge/Lenguajes%20Formales-FIUBA-blue)](https://campus.fi.uba.ar/course/view.php?id=210)

# Intérprete de Scheme en Clojure

Este trabajo práctico fue realizado para la materia de **Lenguajes Formales (75.14)** dictada en la **Facultad de Ingeniería** de la **Universidad de Buenos Aires (FIUBA)** durante el segundo cuatrimestre del 2021.

El objetivo del presente trabajo es construir un intérprete de **Scheme** que corra en la JVM (Java Virtual Machine). Por ello, el lenguaje elegido para su implementación es **Clojure**, un lenguaje de programación de propósito general _dialecto_ de *Lisp*.

Scheme es un dialecto minimalista de la familia de lenguajes de programación de _Lisp_, creado durante la década de 1970 en el MIT AI Lab. El intérprete construido se comporta de manera similar a SCM versión 5f2, (C) 1990-2006 FSF. 

Con este trabajo  práctico, se buscó adquirir conocimientos sobre el **proceso de interpretación de programas** y el **funcionamiento de los intérpretes** de lenguajes de programación y que, además se puso en práctica los conceptos del **paradigma de Programación Funcional** vistos durante el cuatrimestre.


# Ejecución

## Desde Clojure

Se carga el archivo `src/interprete_scheme/scheme.clj`, luego se "ingresa" al namespace de interprete_scheme.scheme y por último se arranca con `(repl)`:

    user=> (load-file "scheme.clj")
    true
    user=> (ns interprete_scheme.scheme)
    nil
    interprete_scheme.scheme=> (repl)
    Interprete de Scheme en Clojure
    Trabajo Practico de 75.14/95.48 - Lenguajes Formales 2021

    Inspirado en:
    SCM version 5f2.
    Copyright (C) 1990-2006 Free Software Foundation.

    > 

En caso de no querer "ingresar" al namespace con `(ns interprete_scheme.scheme)`, se debe comentar la primera línea del archivo `src/interprete_scheme/scheme.clj`:

    ; (ns interprete_scheme.scheme)

Entonces, en ese caso no será requerido "ingresar" a ese namespace, y se ingresará al intérprete solo cargando el archivo y ejecutandolo con `(repl)`:
    
    user=> (load-file "scheme.clj")
    true
    user=> (repl)
    Interprete de Scheme en Clojure
    Trabajo Practico de 75.14/95.48 - Lenguajes Formales 2021

    Inspirado en:
    SCM version 5f2.
    Copyright (C) 1990-2006 Free Software Foundation.

    > 

## Desde Leiningen

En caso de contar con el gestor de proyectos **Leiningen**, la ejecución termina siendo muy sencilla; basta con tipear lo siguiente estando en la carpeta principal de este repositorio

    $ lein run

# Pruebas

## Tests unitarios 

Con **Leiningen** nos permite correr los tests unitarios con el siguiente comando:

    $ lein test

## Tests con archivos provistos

### Demo de Scheme

Para ejecutar este programa ubicado en `/test/interprete_scheme/demo.scm`; se debe ejecutar el intérprete desarrollado y luego cargar dicho programa. Si se usa *Leiningen*:


    $ lein run
    Interprete de Scheme en Clojure
    Trabajo Practico de 75.14/95.48 - Lenguajes Formales 2021

    Inspirado en:
    SCM version 5f2.
    Copyright (C) 1990-2006 Free Software Foundation.

    > (load "test/interprete_scheme/demo.scm")
    
Es autoejecutable. La salida de dicho programa debía coincidir con la salida que se podría ejecutar en el intérprete de Scheme original versión 5f2.

### Problema de las dos jarras

Otro programa pero, es interactivo con el usuario donde su salida también debía coincidir con la del intérprete de Scheme original versión 5f2. Dentro del programa, se debe cargar el archivo `/test/interprete_scheme/jarras.scm`, el mismo programa internamente llamará a otro archivo de `/test/interprete_scheme/breadth.scm`. Luego se ejecuta el programa tipeando `(breadth-first bc)` e indicando los estados iniciales y finales para resolver. Usando de nuevo *Leiningen*.

    $ lein run
    Interprete de Scheme en Clojure
    Trabajo Practico de 75.14/95.48 - Lenguajes Formales 2021

    Inspirado en:
    SCM version 5f2.
    Copyright (C) 1990-2006 Free Software Foundation.

    > (load "test/interprete_scheme/jarras.scm")
    ;loading test/interprete_scheme/jarras.scm 
    ;loading test/interprete_scheme/breadth.scm 
    ;done loading test/interprete_scheme/breadth.scm 
    ;done loading test/interprete_scheme/jarras.scm 
    #<unspecified>
    > (breadth-first bc)               
    Ingrese el estado inicial: (0 0)
    Ingrese el estado   final: (0 4)
    Exito !!!
    Prof ....... 12
    Solucion ... ((0 0) (5 0) (0 5) (5 5) (2 8) (2 0) (0 2) (5 2) (0 7) (5 7) (4 8) (4 0) (0 4))
    #t
    > > 

# Documentos

En la carpeta `/docs` se encuentra el enunciado de este trabajo práctico, junto a todos los documentos provistos por la cátedra que se utilizaron para el desarrollo de este trabajo práctico.

# Licencia

Copyright © 2021 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
