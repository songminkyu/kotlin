// RUN_PIPELINE_TILL: BACKEND
interface A

interface B : A

interface C

interface D : B, C

fun B.foo(): Int = 1
fun <T> T.foo(): String where T : A, T : C = ""

fun takeInt(x: Int) {}

fun test(d: D) {
    val x = d.foo()
    takeInt(x)
}

/* GENERATED_FIR_TAGS: funWithExtensionReceiver, functionDeclaration, integerLiteral, interfaceDeclaration,
localProperty, propertyDeclaration, stringLiteral, typeConstraint, typeParameter */
