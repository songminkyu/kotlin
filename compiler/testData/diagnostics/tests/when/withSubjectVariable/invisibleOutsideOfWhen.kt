// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// LANGUAGE: +VariableDeclarationInWhenSubject
// DIAGNOSTICS: -UNUSED_VARIABLE

fun foo(): Any = 42

fun test(x: Any) {
    val z1 = when (val y = foo()) {
        42 -> "Magic: $y, $x"
        else -> {
            "Not magic: $y, $x"
        }
    }
    val z2 = "Anyway, it was $<!UNRESOLVED_REFERENCE!>y<!>"
}

/* GENERATED_FIR_TAGS: equalityExpression, functionDeclaration, integerLiteral, localProperty, propertyDeclaration,
stringLiteral, whenExpression, whenWithSubject */
