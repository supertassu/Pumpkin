package me.tassu.util

import me.tassu.Pumpkin
import org.spongepowered.api.Sponge
import org.spongepowered.api.text.serializer.TextSerializers

object PumpkinLog {

    private val logger = Pumpkin.container.logger
    private val debug get() = Pumpkin.debug

    fun info(string: String) {
        Sponge.getGame().server.console.sendMessage(TextSerializers.FORMATTING_CODE.deserialize("&6(Pumpkin)&r $string"))
    }

    fun debug(string: String, module: String = "") {
        if (!debug) return

        info("&e[DEBUG]${if (module.isNotEmpty()) " $module" else ""} &r$string")
    }

    fun error(string: String, throwable: Throwable? = null) {
        if (throwable != null) {
            logger.error(string, throwable)
        } else {
            logger.error(string)
        }
    }

    fun warn(string: String) {
        logger.warn(string)
    }

}