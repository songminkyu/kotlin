// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
interface A
interface B: A
interface D

interface BaseSuper<out T>
interface BaseImpl: BaseSuper<D>
interface DerivedSuper<out S>: <!INCONSISTENT_TYPE_PARAMETER_VALUES!>BaseSuper<S>, BaseImpl<!>

fun test(t: BaseSuper<B>) = t is DerivedSuper<A>

/* GENERATED_FIR_TAGS: functionDeclaration, interfaceDeclaration, isExpression, nullableType, out, typeParameter */
