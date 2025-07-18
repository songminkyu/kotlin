// RUN_PIPELINE_TILL: BACKEND
// DIAGNOSTICS: -UNUSED_PARAMETER, -SENSELESS_COMPARISON, -DEBUG_INFO_SMARTCAST

fun takeNotNull(s: String) {}
fun <T> notNull(): T = TODO()
fun <T> nullable(): T? = null
fun <T> dependOn(x: T) = x

fun test() {
    takeNotNull(notNull()!!)
    takeNotNull(nullable()!!)

    var x: String? = null
    takeNotNull(dependOn(x)!!)
    takeNotNull(dependOn(dependOn(x))!!)
    takeNotNull(dependOn(dependOn(x)!!))
    takeNotNull(dependOn(dependOn(x!!)))

    if (x != null) {
        takeNotNull(dependOn(x)<!UNNECESSARY_NOT_NULL_ASSERTION!>!!<!>)
        takeNotNull(dependOn(dependOn(x))<!UNNECESSARY_NOT_NULL_ASSERTION!>!!<!>)
        takeNotNull(dependOn(dependOn(x)<!UNNECESSARY_NOT_NULL_ASSERTION!>!!<!>))
    }
}

/* GENERATED_FIR_TAGS: checkNotNullCall, equalityExpression, functionDeclaration, ifExpression, localProperty,
nullableType, propertyDeclaration, smartcast, typeParameter */
