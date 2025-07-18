// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// SKIP_TXT
fun <T> r(x: suspend () -> T): T = null!!

fun nonReproducer1(): String = r {
    MyObject.someProperty()
}

object MyObject {
    val someProperty: String = "TODO()"
}

suspend operator fun <R> String.invoke(): R = null!!

/* GENERATED_FIR_TAGS: checkNotNullCall, funWithExtensionReceiver, functionDeclaration, functionalType, lambdaLiteral,
nullableType, objectDeclaration, operator, propertyDeclaration, stringLiteral, suspend, typeParameter */
