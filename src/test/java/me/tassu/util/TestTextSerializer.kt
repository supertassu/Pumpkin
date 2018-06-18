package me.tassu.util

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializer
import java.util.regex.Pattern

object TestTextSerializer : TextSerializer {

    override fun getName(): String {
        return "TestTextSerializer"
    }

    override fun getId(): String {
        return "tassu:test/text_serializer"
    }

    /**
     * Translates a string using an alternate color code character into a
     * string that uses the internal ChatColor.COLOR_CODE color code
     * character. The alternate color code character will only be replaced if
     * it is immediately followed by 0-9, A-F, a-f, K-O, k-o, R or r.
     *
     * @param altColorChar The alternate color code character to replace. Ex: &amp;
     * @param textToTranslate Text containing the alternate color code character.
     * @return Text containing the ChatColor.COLOR_CODE color code character.
     * @author The Spigot Team
     */
    private fun translateAlternateColorCodes(altColorChar: Char, colorChar: Char, textToTranslate: String): String {
        val b = textToTranslate.toCharArray()
        for (i in 0 until b.size - 1) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = colorChar
                b[i + 1] = Character.toLowerCase(b[i + 1])
            }
        }
        return String(b)
    }

    override fun serialize(text: Text?): String {
        return translateAlternateColorCodes('&', '§', text!!.toString().replaceFirst("Text{", "").trimEnd('}'))
    }

    override fun deserialize(input: String?): Text {
        return Text.of(translateAlternateColorCodes('§', '&', input!!))
    }
}