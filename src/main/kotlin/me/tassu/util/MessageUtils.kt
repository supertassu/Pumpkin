package me.tassu.util

import me.tassu.Pumpkin
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.text.TextTemplate

fun CommandSource.sendMessage(template: TextTemplate, vararg placeholders: Pair<String, Any>) {
    val map = placeholders.map { it.first to it.second.text() }.toMap().toMutableMap()
    val message = template.apply(map)
    this.sendMessage(Pumpkin.messages.prefix.toBuilder().append(message.build()).build())
}