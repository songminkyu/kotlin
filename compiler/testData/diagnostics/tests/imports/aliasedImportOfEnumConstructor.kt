// RUN_PIPELINE_TILL: BACKEND
// ISSUE: KT-56624
import A as B
enum class A {
    E<!UNRESOLVED_REFERENCE!><!>()
}

/* GENERATED_FIR_TAGS: enumDeclaration, enumEntry */
