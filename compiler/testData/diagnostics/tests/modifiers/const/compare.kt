// RUN_PIPELINE_TILL: BACKEND
const val INT = 0
const val LONG = 0L

const val compareInt1 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER!>1 < 2<!>
const val compareInt2 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER!>1 <= 3<!>
const val compareInt3 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER!>1 > 4<!>
const val compareInt4 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER!>1 >= 5<!>

const val compareInt5 = INT < 2
const val compareInt6 = INT <= 3
const val compareInt7 = INT > 4
const val compareInt8 = INT >= 5

const val compareLong1 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER!>1L < 2L<!>
const val compareLong2 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER!>1L <= 3L<!>
const val compareLong3 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER!>1L > 4L<!>
const val compareLong4 = <!CONST_VAL_WITH_NON_CONST_INITIALIZER!>1L >= 5L<!>

const val compareLong5 = LONG < 2L
const val compareLong6 = LONG <= 3L
const val compareLong7 = LONG > 4L
const val compareLong8 = LONG >= 5L

/* GENERATED_FIR_TAGS: comparisonExpression, const, integerLiteral, propertyDeclaration */
