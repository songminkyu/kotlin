// RUN_PIPELINE_TILL: BACKEND
// FILE: B.kt

package b

abstract class MyClass

fun foo() {}

fun I() {}
interface I {}


// FILE: A.kt

package a
import b.MyClass
import b.foo
import b.I

class YourClass : MyClass()

fun bar() {
    foo()
    I()
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, interfaceDeclaration */
