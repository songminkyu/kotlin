// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER
// ISSUE: KT-35959

interface Expect<T>

inline val <T> Expect<T>.and: Expect<T> get() = this
infix fun <T> Expect<T>.and(assertionCreator: Expect<T>.() -> Unit): Expect<T> = this

fun <K> id(x: K): K = x

fun test(): Expect<Int>.(Expect<Int>.() -> Unit) -> Expect<Int> = id(Expect<Int>::and)

/* GENERATED_FIR_TAGS: callableReference, funWithExtensionReceiver, functionDeclaration, functionalType, getter, infix,
interfaceDeclaration, nullableType, propertyDeclaration, propertyWithExtensionReceiver, thisExpression, typeParameter,
typeWithExtension */
