package me.tassu.msg

import me.tassu.cfg.Configuration
import me.tassu.cfg.FileConfigurationLoader
import me.tassu.util.PumpkinLog

object MessageManager {

    private lateinit var config: Configuration

    private val messages: MutableMap<String, Message> = mutableMapOf()

    fun init() {
        config = FileConfigurationLoader.load("messages")
    }

    fun register(message: Message): Message {
        if (config.contains("pumpkin.messages.${message.id}")) {
            if (config.isString("pumpkin.messages.${message.id}")) {
                message.string = listOf(config.getString("pumpkin.messages.${message.id}"))
            } else {
                message.string = config.getStringList("pumpkin.messages.${message.id}", true)
            }

            messages[message.id] = message
        } else {
            PumpkinLog.warn("[CONF] ${message.id} is not stored on the configuration.")
            PumpkinLog.warn("[CONF] Please update it to the configuration. ")
            PumpkinLog.warn("[CONF] ")
            PumpkinLog.warn("[CONF] *** Default Value: ")
            message.string.forEach { PumpkinLog.warn("[CONF] -> $it") }
            PumpkinLog.warn("[CONF] *** END OF DEFAULT VALUE")
        }

        return message
    }

    fun reload() {
        messages.forEach { _, it -> register(it) }
    }

}