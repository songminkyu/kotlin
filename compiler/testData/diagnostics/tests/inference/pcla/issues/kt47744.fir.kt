// RUN_PIPELINE_TILL: BACKEND
// LANGUAGE: -UnrestrictedBuilderInference
// DIAGNOSTICS: -OPT_IN_IS_NOT_ENABLED -UNCHECKED_CAST
// WITH_STDLIB

import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class)
fun <T> flow(block: suspend FlowCollector<T>.() -> Unit) = Flow<T>()

@OptIn(ExperimentalTypeInference::class)
fun <E> produce(block: suspend SendChannel<E>.() -> Unit) {}

interface SendChannel<in E> {
    val onSend: SelectClause2<E, SendChannel<E>>
}

interface SelectClause2<in P, out Q>
class Flow<out T>
interface FlowCollector<in T>

interface SelectBuilder<in R> {
    operator fun <P, Q> SelectClause2<P, Q>.invoke(param: P, block: suspend (Q) -> R)
}

inline fun <R> select(crossinline builder: SelectBuilder<R>.() -> Unit) = Unit as R

fun test() {
    val x: Flow<String> = flow {
        produce {
            select<Unit> {
                onSend("") {

                }
            }
        }
    }
}

/* GENERATED_FIR_TAGS: asExpression, classDeclaration, classReference, crossinline, funWithExtensionReceiver,
functionDeclaration, functionalType, in, inline, interfaceDeclaration, lambdaLiteral, localProperty, nullableType,
operator, out, propertyDeclaration, stringLiteral, suspend, typeParameter, typeWithExtension */
