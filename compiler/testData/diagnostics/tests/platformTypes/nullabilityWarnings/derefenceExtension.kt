// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
// DIAGNOSTICS: -UNUSED_PARAMETER

// FILE: J.java

import org.jetbrains.annotations.*;

public class J {
    @NotNull
    public static J staticNN;
    @Nullable
    public static J staticN;
    public static J staticJ;
}

// FILE: k.kt

fun test() {
    // @NotNull platform type
    val platformNN = J.staticNN
    // @Nullable platform type
    val platformN = J.staticN
    // platform type with no annotation
    val platformJ = J.staticJ

    platformNN.foo()
    platformN<!UNSAFE_CALL!>.<!>foo()
    platformJ.foo()

    with(platformNN) {
        foo()
    }
    with(platformN) {
        <!UNSAFE_CALL!>foo<!>()
    }
    with(platformJ) {
        foo()
    }

    platformNN.bar()
    platformN.bar()
    platformJ.bar()
}

fun J.foo() {}
fun J?.bar() {}

/* GENERATED_FIR_TAGS: flexibleType, funWithExtensionReceiver, functionDeclaration, javaProperty, javaType,
lambdaLiteral, localProperty, nullableType, propertyDeclaration */
