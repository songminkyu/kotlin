// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: A.java
public @interface A {
    Class<?> arg() default Integer.class;
    int x();
}

// FILE: b.kt
@A(arg = String::class, x = 4) class MyClass2
@A(x = 5) class MyClass3

/* GENERATED_FIR_TAGS: classDeclaration, classReference, integerLiteral, javaType */
