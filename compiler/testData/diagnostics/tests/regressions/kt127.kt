// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// KT-127 Support extension functions in when expressions

class Foo() {}

fun Any?.equals1(other : Any?) : Boolean = true

fun main() {

    val command : Foo? = null

    // Commented for KT-621
    // when (command) {
    //   .equals(null) => 1; // must be resolved
    //   ?.equals(null) => 1 // same here
    // }
    command.equals1(null)
    command?.equals(null)
}

/* GENERATED_FIR_TAGS: classDeclaration, funWithExtensionReceiver, functionDeclaration, localProperty, nullableType,
primaryConstructor, propertyDeclaration, safeCall */
