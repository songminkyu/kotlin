// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +ApproximateAnonymousReturnTypesInPrivateInlineFunctions

private inline fun foo1(crossinline f: () -> Int) = object {
    fun bar(): Int = f()
}

interface I1
interface I2

private inline fun foo2(crossinline f: () -> Int) = object : I1 {
    fun bar(): Int = f()
}

private inline fun <!AMBIGUOUS_ANONYMOUS_TYPE_INFERRED!>foo3<!>(crossinline f: () -> Int) = object : I1, I2 {
    fun bar(): Int = f()
}

private fun foo4(f: () -> Int) = object {
    fun bar(): Int = f()
}

fun test1(b: Boolean) {
    var x = <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>foo1 { 1 }<!>
    if (b) {
        x = <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Any")!>foo1 { 2 }<!>
    }
    x.<!UNRESOLVED_REFERENCE!>bar<!>()
}

fun test2(b: Boolean) {
    var x = <!DEBUG_INFO_EXPRESSION_TYPE("I1")!>foo2 { 1 }<!>
    if (b) {
        x = <!DEBUG_INFO_EXPRESSION_TYPE("I1")!>foo2 { 2 }<!>
    }
    x.<!UNRESOLVED_REFERENCE!>bar<!>()
}

fun test3(b: Boolean) {
    var x = <!DEBUG_INFO_EXPRESSION_TYPE("I1")!>foo3 { 1 }<!>
    if (b) {
        x = <!DEBUG_INFO_EXPRESSION_TYPE("I1")!>foo3 { 2 }<!>
    }
    x.<!UNRESOLVED_REFERENCE!>bar<!>()
}

fun test4(b: Boolean) {
    var x = <!DEBUG_INFO_EXPRESSION_TYPE("<anonymous>")!>foo4 { 1 }<!>
    if (b) {
        x = <!DEBUG_INFO_EXPRESSION_TYPE("<anonymous>")!>foo4 { 2 }<!>
    }
    x.bar()
}

/* GENERATED_FIR_TAGS: anonymousObjectExpression, assignment, crossinline, functionDeclaration, functionalType,
ifExpression, inline, integerLiteral, interfaceDeclaration, lambdaLiteral, localProperty, propertyDeclaration */
