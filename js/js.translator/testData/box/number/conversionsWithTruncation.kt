package foo

fun box(): String {

    assertEquals(65, 321L.toByte())
    assertEquals(-56, 200L.toByte())

    assertEquals(65, 321.toByte())
    assertEquals(-56, 200.toByte())

    assertEquals(65, (321.toShort()).toByte())
    assertEquals(-56, (200.toShort()).toByte())

    assertEquals(-1, 65535L.toShort())
    assertEquals(-1, 65535.toShort())

    assertEquals(65535, 65535.2.toInt())
    assertEquals(23, 23.6f.toInt())

    assertEquals('\u0419', (-654311).toInt().toChar())
    assertEquals('\u0419', (-654311.0).toInt().toChar())
    assertEquals('\u0419', (-654311.0f).toInt().toChar())

    val longX: Long = 9223372034707292481L
    assertEquals("9223372034707292481", longX.toString())

    assertEquals(-2147483327, longX.toInt())
    assertEquals(321, longX.toShort())
    assertEquals(65, longX.toByte())
    assertEquals('\u0141', longX.toInt().toChar())

    val intX: Int = longX.toInt()
    assertEquals(321, intX.toShort())
    assertEquals(65, intX.toByte())

    return "OK"
}
