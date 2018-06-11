package me.tassu.util

import me.tassu.Pumpkin
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.logging.Level

object PumpkinLog {

    private val logger = Pumpkin.instance.logger
    private val debug get() = Pumpkin.debug

    fun info(string: String) {
        Bukkit.getConsoleSender().sendMessage("${Pumpkin.instance.description.prefix ?: Pumpkin.instance.description.name} $string")
    }

    fun debug(string: String, module: String = "") {
        if (!debug) return

        info("${ChatColor.YELLOW}[DEBUG]${if (module.isNotEmpty()) " $module" else ""} ${ChatColor.RESET}$string")
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