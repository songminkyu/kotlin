// RUN_PIPELINE_TILL: FRONTEND
// FIR_DUMP
@RequiresOptIn
annotation class ExperimentalKotlinAnnotation

internal fun interface StableInterface {
    @ExperimentalKotlinAnnotation // @ExperimentalStdlibApi
    fun experimentalMethod()
}

fun regressionTestOverrides() {
    val anonymous: StableInterface = object : StableInterface {
        override fun <!OPT_IN_OVERRIDE_ERROR!>experimentalMethod<!>() {} // correctly fails check
    }
    val lambda = <!OPT_IN_USAGE_ERROR!>StableInterface<!> {} // this does not get flagged
}

@ExperimentalKotlinAnnotation
fun suppressed() {
    val lambda = StableInterface {}
}

/* GENERATED_FIR_TAGS: annotationDeclaration, anonymousObjectExpression, funInterface, functionDeclaration,
interfaceDeclaration, lambdaLiteral, localProperty, override, propertyDeclaration */
