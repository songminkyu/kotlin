// RUN_PIPELINE_TILL: FRONTEND
enum class E <!NON_PRIVATE_CONSTRUCTOR_IN_ENUM!>public<!> constructor(val x: Int) {
    FIRST();

    <!NON_PRIVATE_CONSTRUCTOR_IN_ENUM!>internal<!> constructor(): this(42)

    constructor(y: Int, z: Int): this(y + z)
}

/* GENERATED_FIR_TAGS: additiveExpression, enumDeclaration, enumEntry, integerLiteral, primaryConstructor,
propertyDeclaration, secondaryConstructor */
