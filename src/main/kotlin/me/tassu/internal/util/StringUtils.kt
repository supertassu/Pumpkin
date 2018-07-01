package me.tassu.internal.util

import com.google.common.io.CharStreams
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers
import java.io.InputStream

@JvmName("processColors")
fun String.replaceColors(): Text {
    return TextSerializers.FORMATTING_CODE.deserialize(this)
}

@JvmName("text")
fun Any.text(): Text {
    if (this is Text) return this
    return Text.of(this)
}

@JvmName("asString")
fun Text.string(): String {
    return TextSerializers.FORMATTING_CODE.serialize(this)
}

@JvmName("valueOf")
fun valueOf(any: Any?): String {
    if (any == null) return "null"
    if (any is Entity && any !is Player) {
        any.get(Keys.DISPLAY_NAME).orElse(any.type.translation.id.text()).string()
    } else if (any is Player) {
        any.get(Keys.DISPLAY_NAME).orElse(any.name.text()).string()
    } else if (any is CommandSource) {
        any.name
    }

    return any.toString()
}

@JvmName("convertToString")
fun InputStream.readAsString(): String {
    return CharStreams.toString(this.reader())
}

operator fun Any.plus(s: String): Any = this.toString() + s