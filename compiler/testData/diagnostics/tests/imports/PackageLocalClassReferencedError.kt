// RUN_PIPELINE_TILL: FRONTEND
// FILE: File1.kt
package pack1

private open class SomeClass

// FILE: Main.kt
package a

import pack1.*

private class X : <!INVISIBLE_MEMBER, INVISIBLE_REFERENCE!>SomeClass<!>()

/* GENERATED_FIR_TAGS: classDeclaration */
