// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// ISSUE: KT-61076
// FILE: InspectionApplicationBase.java

public class InspectionApplicationBase {
    private String loadInspectionProfile() { return ""; }
}

// FILE: Main.kt

class QodanaInspectionApplication: InspectionApplicationBase() {
    suspend fun loadInspectionProfile(): String = ""
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, javaType, stringLiteral, suspend */
