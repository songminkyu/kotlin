// RUN_PIPELINE_TILL: BACKEND
package c

fun test() {
    val x = 10
    fun inner1() {
        fun inner2() {
            fun inner3() {
                val <!UNUSED_VARIABLE!>y<!> = x
            }
        }
    }
}

/* GENERATED_FIR_TAGS: functionDeclaration, integerLiteral, localFunction, localProperty, propertyDeclaration */
