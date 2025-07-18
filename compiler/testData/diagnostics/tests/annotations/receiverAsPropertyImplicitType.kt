// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
@Target(AnnotationTarget.TYPE)
annotation class Anno(val position: String)

val List<@Anno("context receiver type $prop") Int>.property get() = this

const val prop = "str"

/* GENERATED_FIR_TAGS: annotationDeclaration, const, getter, primaryConstructor, propertyDeclaration,
propertyWithExtensionReceiver, stringLiteral, thisExpression */
