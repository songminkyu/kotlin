// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// ISSUE: KT-57195

import java.io.File

open class AbstractFE1UastTest {
    open var testDataDir = File("").parentFile
}

class Legacy: AbstractFE1UastTest() {
    override var testDataDir: File = File("").parentFile // K1: ok, K2: VAR_TYPE_MISMATCH_ON_OVERRIDE
}

/* GENERATED_FIR_TAGS: classDeclaration, flexibleType, javaFunction, javaProperty, override, propertyDeclaration,
stringLiteral */
