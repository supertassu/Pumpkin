package me.tassu.util

import com.google.common.io.CharStreams
import me.tassu.Pumpkin
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.text.Text
import java.io.InputStream

@JvmName("processColors")
fun String.replaceColors(): Text {
    return Pumpkin.textSerializer.deserialize(this)
}

@JvmName("text")
fun String.text(): Text {
    return Text.of(this)
}

@JvmName("asString")
fun Text.string(): String {
    return Pumpkin.textSerializer.serialize(this)
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