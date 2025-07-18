// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +ContextParameters

class Ctx {
    val str: String? = ""
}

context(ctx: Ctx)
val prop: String?
    get() = ctx.str

context(ctx: Ctx)
fun test() {
    if (prop is String) {
        <!SMARTCAST_IMPOSSIBLE!>prop<!>.length
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, functionDeclarationWithContext, getter, ifExpression,
isExpression, nullableType, propertyDeclaration, propertyDeclarationWithContext, smartcast, stringLiteral */
