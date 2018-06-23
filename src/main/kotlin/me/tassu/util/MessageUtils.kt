package me.tassu.util

import me.tassu.msg.GeneralMessages
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers

operator fun Text.plus(other: Text): Text = this.concat(other)

fun prefix(): Text = TextSerializers.FORMATTING_CODE.deserialize(GeneralMessages.msgPrefix)

fun CommandSource.sendMessage(input: String, vararg placeholders: Pair<String, Any>) {
    var string = input

    placeholders.forEach {
        string = string.replace("{{${it.first}}}", it.second.toString())
    }

    var text = TextSerializers.FORMATTING_CODE.deserialize(string)

    if (string.startsWith("NOPREFIX|")) {
        text = TextSerializers.FORMATTING_CODE.deserialize(string.replaceFirst("NOPREFIX|", ""))
    }

    this.sendMessage(prefix() + text)
}