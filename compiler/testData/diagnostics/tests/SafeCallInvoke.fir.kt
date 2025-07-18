// RUN_PIPELINE_TILL: BACKEND
class Rule(val apply:() -> Unit)

fun bar() {}

fun foo() {
    val rule: Rule? = Rule { bar() }

    // this compiles and works
    val apply = rule?.apply
    if (apply != null) apply()

    // this compiles and works
    rule?.apply?.invoke()

    // this should be an error
    rule?.apply()

    // these both also ok (with smart cast / unnecessary safe call)
    if (rule != null) {
        rule.apply()
        rule<!UNNECESSARY_SAFE_CALL!>?.<!>apply()
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, equalityExpression, functionDeclaration, functionalType, ifExpression,
lambdaLiteral, localProperty, nullableType, primaryConstructor, propertyDeclaration, safeCall, smartcast */
