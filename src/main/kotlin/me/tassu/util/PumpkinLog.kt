package me.tassu.util

import me.tassu.Pumpkin
import java.util.logging.Level

object PumpkinLog {

    private val logger = Pumpkin.instance.logger

    fun info(string: String) {
        logger.info(string)
    }

    fun error(string: String, throwable: Throwable? = null) {
        if (throwable != null) {
            logger.log(Level.SEVERE, string, throwable)
        } else {
            logger.severe(string)
        }
    }

    fun warn(string: String) {
        logger.warning(string)
    }

}