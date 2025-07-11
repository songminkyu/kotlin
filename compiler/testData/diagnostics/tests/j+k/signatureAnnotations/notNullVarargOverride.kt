// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: BaseClass.java
import org.jetbrains.annotations.NotNull;

public class BaseClass {
    public void loadCache(@NotNull Object... args) {}
}

// FILE: main.kt

class A : BaseClass() {
    // org.jetbrains.annotations.NotNull has @Target PARAMETER, so it doesn't affect elements type
    override fun loadCache(vararg args: Any?) {
        super.loadCache(*args)
    }
}

class B : BaseClass() {
    // org.jetbrains.annotations.NotNull has @Target PARAMETER, so it doesn't affect elements type
    override fun loadCache(vararg args: Any) {
        super.loadCache(*args)
    }
}

/* GENERATED_FIR_TAGS: classDeclaration, functionDeclaration, javaFunction, javaType, nullableType, outProjection,
override, superExpression, vararg */
