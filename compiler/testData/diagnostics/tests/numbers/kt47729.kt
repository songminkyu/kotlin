// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// ISSUE: Kt-47447, KT-47729

fun takeLong(value : Long) {}
fun takeInt(value : Int) {}
fun takeAny(value : Any) {}
fun takeLongX(value : Long?) {}
fun takeIntX(value : Int?) {}
fun takeAnyX(value : Any?) {}
fun <A> takeGeneric(value : A) {}
fun <A> takeGenericX(value : A?) {}

fun test_1() {
    takeLong(1 + 1) // ok
    takeInt(1 + 1) // ok
    takeAny(1 + 1) // ok
    takeLongX(1 + 1) // ok
    takeIntX(1 + 1) // ok
    takeAnyX(1 + 1) // ok
    takeGeneric(1 + 1) // ok
    takeGenericX(1 + 1) // ok
}

/* GENERATED_FIR_TAGS: functionDeclaration, integerLiteral, nullableType, typeParameter */
