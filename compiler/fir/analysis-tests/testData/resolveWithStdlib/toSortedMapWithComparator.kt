// RUN_PIPELINE_TILL: BACKEND
fun test(map: Map<String?, List<String>>) {
    val sortedMap = map.toSortedMap(nullsLast())
}

/* GENERATED_FIR_TAGS: functionDeclaration, localProperty, nullableType, propertyDeclaration */
