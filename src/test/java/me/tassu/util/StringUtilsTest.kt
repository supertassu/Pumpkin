package me.tassu.util

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.nio.charset.StandardCharsets

@RunWith(MockitoJUnitRunner::class)
class StringUtilsTest {

    @Test
    fun testProcessColors() {
        assertEquals("§casd heyy!", "&casd heyy!".replaceColors())
        assertEquals("&&", "&&".replaceColors())
        assertEquals("& c", "& c".replaceColors())
        assertEquals("§c", "&c".replaceColors())
    }

    @Test
    fun basicValueOf() {
        assertEquals("asd", valueOf("asd"))
        assertEquals("123", valueOf(123))
    }

    @Test
    fun nullValueOf() {
        assertEquals("null", valueOf(null))
    }

    /*@Test
    fun entityValueOf() {
        val entity = Mockito.mock(Entity::class.java)
        Mockito.`when`(entity.name).thenReturn("hello there")
        assertEquals("hello there", valueOf(entity))

        Mockito.`when`(entity.customName).thenReturn("a name")
        assertEquals(false, entity.isCustomNameVisible)
        assertEquals("hello there", valueOf(entity))

        Mockito.`when`(entity.isCustomNameVisible).thenReturn(true)
        assertEquals("a name", valueOf(entity))
    }

    @Test
    fun commandSenderValueOf() {
        val sender = Mockito.mock(ConsoleCommandSender::class.java)
        Mockito.`when`(sender.name).thenReturn("CONSOLE")
        assertEquals("CONSOLE", valueOf(sender))
    }

    @Test
    fun playerValueOf() {
        val player = Mockito.mock(Player::class.java)
        Mockito.`when`(player.name).thenReturn("testing")
        assertEquals("testing", valueOf(player))

        Mockito.`when`(player.displayName).thenReturn("not name")
        assertEquals("not name", valueOf(player))
    }*/

    @Test
    fun testConvertToString() {
        val inputStream = "this is a great string 123 ASD".byteInputStream(StandardCharsets.UTF_8)
        assertEquals("this is a great string 123 ASD", inputStream.readAsString())
    }


}