// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
@Suppress("warnings")
class C {
    fun foo(p: String??) {
        // Make sure errors are not suppressed:
        <!VAL_REASSIGNMENT!>p<!> = ""
    }
}

/* GENERATED_FIR_TAGS: assignment, classDeclaration, functionDeclaration, nullableType, stringLiteral */
