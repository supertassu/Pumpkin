package me.tassu.util

import org.junit.Test

import org.junit.Assert.*
import java.io.IOException

class ErrorUtilsTest {

    @Test
    fun testThrows() {
        assertTrue({
            throw RuntimeException()
        }.throws())

        assertTrue({
            throw RuntimeException()
        }.throws(RuntimeException::class.java))

        assertFalse({
            throw IllegalAccessError()
        }.throws(IOException::class.java))

        assertFalse({}.throws())
        assertFalse({}.throws(Throwable::class.java))
    }

    @Test
    fun testDoesNotThrow() {
        assertTrue({
            throw IllegalArgumentException()
        }.doesNotThrow(IOException::class.java))

        assertTrue("nothing should not throw anything", {}.doesNotThrow())

        assertFalse({
            throw IOException()
        }.doesNotThrow(IOException::class.java))

        assertFalse({
            throw IOException()
        }.doesNotThrow(IOException::class.java))
    }
}