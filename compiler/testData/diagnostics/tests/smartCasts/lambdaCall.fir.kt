// RUN_PIPELINE_TILL: BACKEND
// See KT-9134: smart cast is not provided inside lambda call
fun bar(): Int = {
    var i: Int?
    i = 42
    i
}()

/* GENERATED_FIR_TAGS: assignment, functionDeclaration, integerLiteral, lambdaLiteral, localProperty, nullableType,
propertyDeclaration, smartcast */
