// RUN_PIPELINE_TILL: FRONTEND
open class Foo {}
open class Bar {}

fun <T : Bar, T1> foo(x : Int) {}
fun <T1, T : Foo> foo(x : Long) {}

fun f(): Unit {
    foo<<!UPPER_BOUND_VIOLATED!>Int<!>, Int>(1)
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, integerLiteral, nullableType, typeConstraint,
typeParameter */
