// RUN_PIPELINE_TILL: FRONTEND
// ISSUE: KT-72740
// COMPARE_WITH_LIGHT_TREE
annotation class Anno(val s: String)

@Anno("Use 'AAA' instead"
<!UNRESOLVED_REFERENCE!>open<!> <!EXPRESSION_EXPECTED{PSI}!>class MyClass : Any() {
    val foo = 24

    @Anno("str")
    fun baz() {

    }

    <!WRONG_MODIFIER_CONTAINING_DECLARATION{PSI}!>companion<!> object {
        @Anno("something")
        fun getSomething(a: Int = 24) {

        }
    }
}<!><!SYNTAX, SYNTAX!><!>

/* GENERATED_FIR_TAGS: annotationDeclaration, classDeclaration, companionObject, functionDeclaration, integerLiteral,
localClass, objectDeclaration, primaryConstructor, propertyDeclaration, stringLiteral */
