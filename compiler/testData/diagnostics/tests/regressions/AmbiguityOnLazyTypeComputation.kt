// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// One of the two passes is making a scope and turning vals into functions
// See KT-76

package x

val b : Foo = Foo()
val a1 = b.compareTo(2)

class Foo() {
  fun compareTo(other : Byte)   : Int = 0
  fun compareTo(other : Char)   : Int = 0
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, integerLiteral, primaryConstructor, propertyDeclaration */
