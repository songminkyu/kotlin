// RUN_PIPELINE_TILL: FRONTEND
// LANGUAGE: +ContextParameters

open class Base
class A

class Test1 <!UNSUPPORTED!>context(c: A)<!> constructor(firstName: String)

class Test2 {
    <!UNSUPPORTED!>context(c: A)<!>
    constructor() {}

    <!UNSUPPORTED!>context(c: A)<!>
    constructor(a: String) {}
}

class Test3: Base {
    constructor(a: String) :<!SYNTAX!><!> context(c: A) <!SYNTAX!>super<!><!SYNTAX!>(<!><!SYNTAX!>)<!>

    constructor() :<!SYNTAX!><!> context(c: A) <!SYNTAX!>this<!><!SYNTAX!>(<!><!SYNTAX!>"<!><!SYNTAX!>"<!><!SYNTAX!>)<!>
}

class Test4(val a: context(A) ()-> String = {""}) {
    constructor(b: context(A) (String)-> String) : this() {}
}

class Test5 {
    class Nested {
        <!UNSUPPORTED!>context(c: A)<!>
        constructor(s: String)
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionalType, lambdaLiteral, nestedClass, primaryConstructor,
propertyDeclaration, secondaryConstructor, stringLiteral, typeWithContext */
