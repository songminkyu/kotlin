// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
typealias S = String
typealias SS = S
typealias SSS = SS

val s1: SSS = ""
val s2: SSS? = null
val s3: List<SSS>? = null
val s4: List<List<SSS>?>? = null

/* GENERATED_FIR_TAGS: nullableType, propertyDeclaration, stringLiteral, typeAliasDeclaration */
