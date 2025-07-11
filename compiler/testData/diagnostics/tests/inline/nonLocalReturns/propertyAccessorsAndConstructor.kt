// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
inline fun <R> doCall(p: () -> R) {
    p()
}

inline fun <R> doCallInt(p: () -> R): R {
    return p()
}

class A {
    var result: Int = doCallInt { <!RETURN_NOT_ALLOWED!>return<!> this };

    var field: Int
        get() {
            doCall { return 1 }
            return 2
        }
        set(v: Int) {
            doCall {
                result = v / 2
                return
            }
            result = v
        }
}

/* GENERATED_FIR_TAGS: assignment, classDeclaration, functionDeclaration, functionalType, getter, inline, integerLiteral,
lambdaLiteral, multiplicativeExpression, nullableType, propertyDeclaration, setter, thisExpression, typeParameter */
