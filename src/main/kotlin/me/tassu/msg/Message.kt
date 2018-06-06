package me.tassu.msg

import me.tassu.util.replaceColors
import me.tassu.util.valueOf
import org.bukkit.command.CommandSender

class Message(val id: String, var string: List<String>) {

    companion object {
        fun register(id: String, vararg string: String): Message {
            return MessageManager.register(Message(id, string.toList()))
        }
    }

    fun send(placeholders: Map<String, Any> = mapOf(), vararg targets: CommandSender) {
        var messageString = string.joinToString(separator = System.lineSeparator())

        placeholders.forEach {
            messageString = messageString.replace("{{{ ${it.key} }}}", valueOf(it.value), true)
        }

        val message = messageString.replaceColors().split(System.lineSeparator())

        targets.forEach {
            player -> message.forEach {
                player.sendMessage(it)
            }
        }

    }

    override fun toString(): String {
        return "Message(id='$id', string=$string)"
    }


}
