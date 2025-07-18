// RUN_PIPELINE_TILL: BACKEND
// DUMP_CFG
interface AutoCloseable {
    fun close()
}

internal fun AutoCloseable?.closeFinally(cause: Throwable?) = when {
    this == null -> {}
    cause == null -> close()
    else ->
        try {
            close()
        } catch (closeException: Throwable) {
            cause.addSuppressed(closeException)
        }
}

inline fun <reified T : Any> List<*>.firstIsInstanceOrNull(): T? {
    for (element in this) if (element is T) return element
    return null
}

/* GENERATED_FIR_TAGS: equalityExpression, forLoop, funWithExtensionReceiver, functionDeclaration, ifExpression, inline,
interfaceDeclaration, isExpression, localProperty, nullableType, propertyDeclaration, reified, smartcast, starProjection,
thisExpression, tryExpression, typeConstraint, typeParameter, whenExpression */
