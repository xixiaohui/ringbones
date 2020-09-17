package com.xxh.ringbones

import org.junit.Assert.assertEquals
import org.junit.Test

import com.google.android.material.math.MathUtils

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

}

fun main() {
    var distance = MathUtils.dist(1.0f,2.0f,3.0f,4.0f)

    System.out.println("hello word!$distance")
}