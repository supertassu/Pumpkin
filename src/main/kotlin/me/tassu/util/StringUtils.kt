package me.tassu.util

import com.google.common.io.CharStreams
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import java.io.InputStream

@JvmName("processColors")
fun String.replaceColors(): String {
    return ChatColor.translateAlternateColorCodes('&', this)
}

@JvmName("valueOf")
fun valueOf(any: Any?): String {
    if (any == null) return "null"
    if (any is CommandSender) {
        if (any is Entity && any !is Player) {
            return if (any.isCustomNameVisible) {
                any.customName
            } else {
                any.name
            }
        }

        if (any !is Player) {
            return any.name
        }

        return if (any.displayName == null) {
            any.name
        } else {
            any.displayName
        }
    }
    return any.toString()
}

@JvmName("convertToString")
fun InputStream.readAsString(): String {
    return CharStreams.toString(this.reader())
}