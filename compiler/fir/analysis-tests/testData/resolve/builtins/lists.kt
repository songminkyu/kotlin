// RUN_PIPELINE_TILL: BACKEND
abstract class MyStringList : List<String>
abstract class MyMutableStringList : MutableList<String>

fun List<String>.convert(): MyStringList = this as MyStringList
fun ret(l: MutableList<String>): MyMutableStringList = l as MyMutableStringList

/* GENERATED_FIR_TAGS: asExpression, classDeclaration, funWithExtensionReceiver, functionDeclaration, thisExpression */
