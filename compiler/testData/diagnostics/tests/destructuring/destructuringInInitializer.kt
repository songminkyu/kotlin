// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL

class A {
    operator fun component1() = "O"
    operator fun component2() = "K"
}

class Foo {
    val bar =
        if (true) ""
        else {
            val (o, k) = A()
            o + k
        }

    init {
        val (o, k) = A()
        val r = o + k
    }
}

/* GENERATED_FIR_TAGS: additiveExpression, classDeclaration, destructuringDeclaration, functionDeclaration, ifExpression,
init, localProperty, operator, propertyDeclaration, stringLiteral */
