// RUN_PIPELINE_TILL: FIR2IR
// WITH_KOTLIN_JVM_ANNOTATIONS
// LANGUAGE:+DirectJavaActualization
// MODULE: m1-common
// FILE: common.kt
expect class <!NO_ACTUAL_FOR_EXPECT{JVM}!>Foo<!> { // "direct actual" in different compilation unit is not permitted
    fun foo()
}

expect class A

// MODULE: m2-jvm
// FILE: Foo.java
@kotlin.annotations.jvm.KotlinActual public class Foo {
    @kotlin.annotations.jvm.KotlinActual public void foo() {
    }
}

// MODULE: m3-jvm(m2-jvm)()(m1-common)
// FILE: A.kt
actual class A

/* GENERATED_FIR_TAGS: actual, classDeclaration, expect, functionDeclaration */
