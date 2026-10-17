package com.alpha.battery

import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun temperatureTenthsToCelsius() {
        val tenths = 312
        val celsius = tenths / 10.0
        assertEquals(31.2, celsius, 0.001)
    }
}
