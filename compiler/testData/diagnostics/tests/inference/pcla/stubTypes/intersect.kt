// RUN_PIPELINE_TILL: BACKEND
// LANGUAGE: +UnrestrictedBuilderInference
// DIAGNOSTICS: -UNUSED_PARAMETER -UNCHECKED_CAST -DEPRECATION -OPT_IN_IS_NOT_ENABLED -UNUSED_VARIABLE
// WITH_STDLIB

// FILE: Test.java

class Test {
    static <T> T foo(T x) { return x; }
}

// FILE: main.kt
import kotlin.experimental.ExperimentalTypeInference

@OptIn(ExperimentalTypeInference::class)
fun <R1> build(block: TestInterface<R1>.() -> Unit): R1 = TODO()

@OptIn(ExperimentalTypeInference::class)
fun <R2> build2(block: TestInterface<R2>.() -> Unit): R2 = TODO()

class In<in K>

interface TestInterface<R> {
    fun emit(r: R)
    fun get(): R
    fun getIn(): In<R>
}

fun <U> id(x: U) = x
fun <E> intersect(vararg x: In<E>): E = null as E

fun test() {
    val ret = build {
        emit("1")
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>intersect(getIn(), getIn())<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>intersect(getIn(), Test.foo(getIn()))<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>intersect(Test.foo(getIn()), Test.foo(getIn()))<!>
        <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>intersect(Test.foo(getIn()), getIn())<!>

        build2 {
            emit(1)
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>intersect(<!TYPE_MISMATCH!>this@build.getIn()<!>, getIn())<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>intersect(<!TYPE_MISMATCH!>getIn()<!>, Test.foo(this@build.getIn()))<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.Int")!>intersect(<!TYPE_MISMATCH!>Test.foo(this@build.getIn())<!>, Test.foo(getIn()))<!>
            <!DEBUG_INFO_EXPRESSION_TYPE("kotlin.String")!>intersect(<!TYPE_MISMATCH!>Test.foo(getIn())<!>, this@build.getIn())<!>
            ""
        }
        ""
    }
}

/* GENERATED_FIR_TAGS: asExpression, classDeclaration, classReference, flexibleType, functionDeclaration, functionalType,
in, integerLiteral, interfaceDeclaration, intersectionType, javaFunction, lambdaLiteral, localProperty, nullableType,
propertyDeclaration, stringLiteral, thisExpression, typeParameter, typeWithExtension, vararg */
