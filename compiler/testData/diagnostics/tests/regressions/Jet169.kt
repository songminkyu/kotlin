// RUN_PIPELINE_TILL: BACKEND
fun set(key : String, value : String) {
  val a : String? = ""
  when (a) {
    "" -> <!DEBUG_INFO_SMARTCAST!>a<!>.get(0)
    is String, is Any -> <!DEBUG_INFO_SMARTCAST!>a<!>.compareTo("")
    else -> a.toString()
  }
}

/* GENERATED_FIR_TAGS: disjunctionExpression, equalityExpression, functionDeclaration, integerLiteral, isExpression,
localProperty, nullableType, propertyDeclaration, smartcast, stringLiteral, whenExpression, whenWithSubject */
