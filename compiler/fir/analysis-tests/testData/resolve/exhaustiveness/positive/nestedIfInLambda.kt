// RUN_PIPELINE_TILL: BACKEND
fun run(block: () -> Unit) {}

fun test(b1: Boolean, b2: Boolean) {
    var result = false
    run {
        if (b1)
            if (b2)
                result = true
    }
}

/* GENERATED_FIR_TAGS: assignment, functionDeclaration, functionalType, ifExpression, lambdaLiteral, localProperty,
propertyDeclaration */
