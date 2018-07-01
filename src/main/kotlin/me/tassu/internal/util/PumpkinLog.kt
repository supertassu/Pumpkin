package me.tassu.internal.util

import me.tassu.Pumpkin
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object PumpkinLog {

    private val logger: Logger get() {
        if (Pumpkin.container == null) return LoggerFactory.getLogger("Pumpkin")
        return Pumpkin.container!!.logger
    }

    private val debug get() = Pumpkin.debug

    fun info(string: String) {
        logger.info(string)
    }

    fun debug(string: String, module: String = "") {
        if (!debug) return

        info("[DEBUG${if (module.isNotEmpty()) ": $module]" else "]"} $string")
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
