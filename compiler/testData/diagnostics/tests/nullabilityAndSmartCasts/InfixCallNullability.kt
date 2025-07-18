// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
class A() {
    operator infix fun plus(i : Int) {}
    operator fun unaryMinus() {}
    operator infix fun contains(a : Any?) : Boolean = true
}

operator infix fun A.div(i : Int) {}
operator infix fun A?.times(i : Int) {}

fun test(x : Int?, a : A?) {
    x<!UNSAFE_CALL!>.<!>plus(1)
    x?.plus(1)
    x <!UNSAFE_OPERATOR_CALL!>+<!> 1
    <!UNSAFE_CALL!>-<!>x
    x<!UNSAFE_CALL!>.<!>unaryMinus()
    x?.unaryMinus()

    a<!UNSAFE_CALL!>.<!>plus(1)
    a?.plus(1)
    a <!UNSAFE_INFIX_CALL!>plus<!> 1
    a <!UNSAFE_OPERATOR_CALL!>+<!> 1
    <!UNSAFE_CALL!>-<!>a
    a<!UNSAFE_CALL!>.<!>unaryMinus()
    a?.unaryMinus()

    a<!UNSAFE_CALL!>.<!>div(1)
    a <!UNSAFE_OPERATOR_CALL!>/<!> 1
    a <!UNSAFE_INFIX_CALL!>div<!> 1
    a?.div(1)

    a.times(1)
    a * 1
    a times 1
    a?.times(1)

    1 <!UNSAFE_OPERATOR_CALL!>in<!> a
    a <!UNSAFE_INFIX_CALL!>contains<!> 1
    a<!UNSAFE_CALL!>.<!>contains(1)
    a?.contains(1)
}

/* GENERATED_FIR_TAGS: additiveExpression, classDeclaration, funWithExtensionReceiver, functionDeclaration, infix,
integerLiteral, multiplicativeExpression, nullableType, operator, primaryConstructor, safeCall, unaryExpression */
