// RUN_PIPELINE_TILL: FRONTEND
//FILE:a/C.java
//KT-1942 Package local members from Java are visible in subclasses
package a;

public class C {
    int myValue;
}

//FILE:d.kt

package d

import a.C

class A : C() {
    fun test() {
        val v = <!INVISIBLE_MEMBER!>myValue<!>
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, javaProperty, javaType, localProperty, propertyDeclaration */
