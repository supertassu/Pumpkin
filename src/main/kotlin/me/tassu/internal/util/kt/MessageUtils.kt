package me.tassu.internal.util.kt

import me.tassu.internal.di.PumpkinHolder
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers

operator fun Text.plus(other: Text): Text = this.concat(other)

fun prefix(): Text = TextSerializers.FORMATTING_CODE.deserialize(PumpkinHolder.getInstance().messages.meta.msgPrefix)

fun String.formatColoredMessage(vararg placeholders: Pair<String, Any>): Text {
    return this.formatMessage(true, *placeholders)
}

fun String.formatMessage(color: Boolean = true, vararg placeholders: Pair<String, Any>): Text {
    var string = this

    placeholders.forEach {
        string = if (it.second is Text) {
            string.replace("{{${it.first}}}", (it.second as Text).toPlain())
        } else {
            string.replace("{{${it.first}}}", it.second.toString())
        }
    }

    return if (string.startsWith("NOPREFIX|")) {
        if (color) {
            TextSerializers.FORMATTING_CODE.deserialize(string.replaceFirst("NOPREFIX|", ""))
        } else {
            string.replaceFirst("NOPREFIX|", "").text()
        }
    } else {
        if (color) {
            prefix() + TextSerializers.FORMATTING_CODE.deserialize(string)
        } else {
            prefix() + string.text()
        }
    }
}

fun CommandSource.sendMessage(color: Boolean = true, input: String, vararg placeholders: Pair<String, Any>) {
    this.sendMessage(input.formatMessage(color, *placeholders))
}

fun CommandSource.sendColoredMessage(input: String, vararg placeholders: Pair<String, Any>) {
    this.sendMessage(input.formatColoredMessage(*placeholders))
}

fun CommandSource.sendColoredMessages(input: List<String>, vararg placeholders: Pair<String, Any>) {
    input.forEach {
        sendColoredMessage(it, *placeholders)
    }
}