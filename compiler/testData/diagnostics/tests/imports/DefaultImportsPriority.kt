// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// WITH_EXTRA_CHECKERS

import java.lang.reflect.*
import <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>java.util.List<!>

fun foo(
        p1: Array<String> /* should be resolved to kotlin.Array */,
        p2: <!PLATFORM_CLASS_MAPPED_TO_KOTLIN!>List<String><!> /* should be resolved to java.util.List */) { }

/* GENERATED_FIR_TAGS: functionDeclaration */
