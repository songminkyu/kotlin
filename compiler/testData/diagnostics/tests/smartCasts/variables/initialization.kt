// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
fun foo() {
    var v: Any = "xyz"
    // It is possible in principle to provide smart cast here
    // but now we decide that v is Any
    v.<!UNRESOLVED_REFERENCE!>length<!>()
    v = 42
    v.<!UNRESOLVED_REFERENCE!>length<!>()
}

/* GENERATED_FIR_TAGS: assignment, functionDeclaration, integerLiteral, localProperty, propertyDeclaration, smartcast,
stringLiteral */
