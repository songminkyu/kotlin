// RUN_PIPELINE_TILL: BACKEND
// LANGUAGE: -ProhibitVisibilityOfNestedClassifiersFromSupertypesOfCompanion
// see https://youtrack.jetbrains.com/issue/KT-21515

object WithFunctionInBase {
    abstract class <!CYCLIC_SCOPES_WITH_COMPANION!>DerivedAbstract<!> : C.Base()

    class Data

    public class C {
        // error-scope
        val data: <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE, DEPRECATED_ACCESS_BY_SHORT_NAME!>Data<!> = Data()

        open class <!CYCLIC_SCOPES_WITH_COMPANION!>Base<!>() {
            // error-scope
            fun foo(): <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE, DEPRECATED_ACCESS_BY_SHORT_NAME!>Int<!> = 42
        }

        companion <!CYCLIC_SCOPES_WITH_COMPANION!>object<!> : DerivedAbstract()
    }
}

object WithPropertyInBase {
    // This case is very similar to previous one, but there are subtle differences from POV of implementation

    abstract class <!CYCLIC_SCOPES_WITH_COMPANION!>DerivedAbstract<!> : C.Base()

    class Data

    public class C {

        open class <!CYCLIC_SCOPES_WITH_COMPANION!>Base<!>() {
            // error-scope
            val foo: <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE, DEPRECATED_ACCESS_BY_SHORT_NAME!>Int<!> = 42
        }

        // error-scope
        val data: <!DEBUG_INFO_ELEMENT_WITH_ERROR_TYPE, DEPRECATED_ACCESS_BY_SHORT_NAME!>Data<!> = Data()

        companion <!CYCLIC_SCOPES_WITH_COMPANION!>object<!> : DerivedAbstract()
    }
}

object WithPropertyInBaseDifferentOrder {
    // This case is very similar to previous one, but there are subtle differences from POV of implementation
    // Note how position of property in file affected order of resolve, and, consequently, its results and
    // diagnostics.

    abstract class <!CYCLIC_SCOPES_WITH_COMPANION!>DerivedAbstract<!> : C.Base()

    class Data

    public class C {
        // Now it is successfully resolved (vs. ErrorType like in the previous case)
        val data: Data = Data()

        open class <!CYCLIC_SCOPES_WITH_COMPANION!>Base<!>() {
            // Now it is unresolved (vs. ErrorType like in the previous case)
            val foo: <!UNRESOLVED_REFERENCE!>Int<!> = 42

        }

        companion <!CYCLIC_SCOPES_WITH_COMPANION!>object<!> : DerivedAbstract()
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, companionObject, functionDeclaration, integerLiteral, nestedClass,
objectDeclaration, primaryConstructor, propertyDeclaration */
