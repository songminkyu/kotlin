// RUN_PIPELINE_TILL: BACKEND
// FULL_JDK
// ISSUE: KT-48113
// STATUS: On a K2 technical meeting on Mar-27-2023 it was decided to report a warning
// WITH_EXTRA_CHECKERS

fun collapse(path: String) {
    val result = (path as <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>java.lang.String<!>).replace("123", "456")
    if (result !== path) {}
}

/* GENERATED_FIR_TAGS: asExpression, equalityExpression, flexibleType, functionDeclaration, ifExpression,
intersectionType, javaFunction, localProperty, propertyDeclaration, smartcast, stringLiteral */
