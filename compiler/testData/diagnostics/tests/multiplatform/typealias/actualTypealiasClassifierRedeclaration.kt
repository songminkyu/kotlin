// IGNORE_FIR_DIAGNOSTICS
// RUN_PIPELINE_TILL: BACKEND
// LANGUAGE: +MultiPlatformProjects

// MODULE: common
// FILE: common.kt
expect class <!AMBIGUOUS_ACTUALS{JVM}, PACKAGE_OR_CLASSIFIER_REDECLARATION{JVM}!>A<!> {}

// MODULE: jvm()()(common)
// FILE: main.kt
actual typealias <!PACKAGE_OR_CLASSIFIER_REDECLARATION!>A<!> = B

class B {}

class <!ACTUAL_MISSING, PACKAGE_OR_CLASSIFIER_REDECLARATION!>A<!> {}

/* GENERATED_FIR_TAGS: actual, classDeclaration, expect, typeAliasDeclaration */
