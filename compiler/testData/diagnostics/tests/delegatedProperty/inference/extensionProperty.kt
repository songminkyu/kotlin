// RUN_PIPELINE_TILL: FRONTEND
package foo

import kotlin.reflect.KProperty

open class A {
    val B.w: Int by <!DELEGATE_SPECIAL_FUNCTION_NONE_APPLICABLE!>MyProperty()<!>
}

val B.r: Int by <!DELEGATE_SPECIAL_FUNCTION_NONE_APPLICABLE!>MyProperty()<!>

val A.e: Int by MyProperty()

class B {
    val A.f: Int by MyProperty()
}

class MyProperty<R : A, T> {
    operator fun getValue(thisRef: R, desc: KProperty<*>): T {
        throw Exception("$thisRef $desc")
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, nullableType, operator, propertyDeclaration,
propertyDelegate, propertyWithExtensionReceiver, starProjection, stringLiteral, typeConstraint, typeParameter */
