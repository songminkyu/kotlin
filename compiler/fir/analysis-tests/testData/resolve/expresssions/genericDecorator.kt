// RUN_PIPELINE_TILL: FRONTEND
// FILE: LookupElement.java

public abstract class LookupElement {
    public abstract String getLookupString();
}

// FILE: Decorator.java

public abstract class Decorator<T extends LookupElement> extends LookupElement {
    public T getDelegate() {
        return null;
    }
}

// FILE: test.kt

class MyDecorator : <!SUPERTYPE_NOT_INITIALIZED!>Decorator<LookupElement><!> {
    override fun getLookupString(): String = delegate.lookupString
}

/* GENERATED_FIR_TAGS: classDeclaration, flexibleType, functionDeclaration, javaProperty, javaType, override */
