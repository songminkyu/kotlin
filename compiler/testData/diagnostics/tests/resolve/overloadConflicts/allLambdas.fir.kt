// RUN_PIPELINE_TILL: FRONTEND
// DIAGNOSTICS: -UNUSED_PARAMETER, -DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE, -DEBUG_INFO_MISSING_UNRESOLVED

object X1
object X2

fun <T1> foo(x: T1, f: (T1) -> T1) = X1
fun <T2> foo(xf: () -> T2, f: (T2) -> T2) = X2

val test: X2 = <!OVERLOAD_RESOLUTION_AMBIGUITY!>foo<!>({ 0 }, { <!CANNOT_INFER_VALUE_PARAMETER_TYPE!>it<!> -> it + 1 })

/* GENERATED_FIR_TAGS: additiveExpression, functionDeclaration, functionalType, integerLiteral, lambdaLiteral,
nullableType, objectDeclaration, propertyDeclaration, typeParameter */
