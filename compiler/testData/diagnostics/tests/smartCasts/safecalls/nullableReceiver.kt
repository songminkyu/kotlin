// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// See KT-10056
class Foo(val bar: String)

fun test(foo: Foo?) {
    foo?.bar.let {
        // Error, foo?.bar is nullable
        it<!UNSAFE_CALL!>.<!>length
        // Error, foo is nullable
        foo<!UNSAFE_CALL!>.<!>bar.length
        // Correct
        foo?.bar?.length
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, lambdaLiteral, nullableType, primaryConstructor,
propertyDeclaration, safeCall */
