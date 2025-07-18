// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
fun bar(): Boolean { return true }

fun foo(s: String?): Int {
    while (s==null) {
        if (bar()) break
    }
    // Call is unsafe due to break
    return s<!UNSAFE_CALL!>.<!>length
}

/* GENERATED_FIR_TAGS: break, equalityExpression, functionDeclaration, ifExpression, nullableType, whileLoop */
