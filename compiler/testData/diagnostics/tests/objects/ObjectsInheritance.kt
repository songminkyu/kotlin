// RUN_PIPELINE_TILL: FRONTEND
// FIR_IDENTICAL
package toplevelObjectDeclarations

object CObj {}

object DOjb : <!SINGLETON_IN_SUPERTYPE!>CObj<!> {}

/* GENERATED_FIR_TAGS: objectDeclaration */
