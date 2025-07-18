// RUN_PIPELINE_TILL: FRONTEND
sealed class Base {
    class A : Base() {
        class B : Base()
    }
}

class C : Base()

fun test_1(e: Base) {
    val a = <!NO_ELSE_IN_WHEN!>when<!> (e) {
        is Base.A -> 1
        is Base.A.B -> 2
    }

    val b = <!NO_ELSE_IN_WHEN!>when<!> (e) {
        is Base.A -> 1
        is Base.A.B -> 2
        <!USELESS_IS_CHECK!>is String<!> -> 3
    }

    val c = when (e) {
        is Base.A -> 1
        is Base.A.B -> 2
        is C -> 3
    }

    val d = when (e) {
        is Base.A -> 1
        else -> 2
    }
}

fun test_2(e: Base?) {
    val a = <!NO_ELSE_IN_WHEN!>when<!> (e) {
        is Base.A -> 1
        is Base.A.B -> 2
        is C -> 3
    }

    val b = when (e) {
        is Base.A -> 1
        is Base.A.B -> 2
        is C -> 3
        null -> 4
    }

    val c = when (e) {
        is Base.A -> 1
        is Base.A.B -> 2
        is C -> 3
        else -> 4
    }
}

fun test_3(e: Base) {
    val a = when (e) {
        is Base.A, is Base.A.B -> 1
        is C -> 2
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, disjunctionExpression, equalityExpression, functionDeclaration, integerLiteral,
isExpression, localProperty, nestedClass, nullableType, propertyDeclaration, sealed, smartcast, whenExpression,
whenWithSubject */
