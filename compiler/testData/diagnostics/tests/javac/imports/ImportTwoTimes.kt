// RUN_PIPELINE_TILL: BACKEND
// FIR_IDENTICAL
// FILE: weatherForecast/Weather.java
package weatherForecast;

public class Weather {
    public void test() {}
}

// FILE: a/a.java
package a;

import weatherForecast.Weather;
import weatherForecast.Weather;

public class a {
    public Weather forecast() { return null; }
}

// FILE: a.kt
package a

fun test() = a().forecast().test()

/* GENERATED_FIR_TAGS: flexibleType, functionDeclaration, javaFunction, javaType */
