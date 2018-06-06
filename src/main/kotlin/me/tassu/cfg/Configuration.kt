package me.tassu.cfg

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import com.typesafe.config.ConfigFactory
import me.tassu.util.doesNotThrow
import me.tassu.util.readAsString
import me.tassu.util.replaceColors
import org.bukkit.plugin.Plugin
import java.io.File

class Configuration(private val plugin: Plugin, private val name: String) {

    private val folder: File get() = plugin.dataFolder
    private val file = File(folder, "$name.conf")

    @Suppress("MemberVisibilityCanBePrivate")
    @get:JvmName("getAsTypeSafeConfig")
    val config: Config

    init {
        if (!folder.exists()) {
            folder.mkdir()
        }

        if (!file.exists()) {
            plugin.saveResource("$name.conf", false)
        }

        config = ConfigFactory.parseFile(file).withFallback(
                ConfigFactory.parseString(plugin.getResource("$name.conf").readAsString()))
    }

    companion object {
        private val WRONG_TYPE_EXCEPTION: Class<out Throwable> = ConfigException.WrongType::class.java

    }
    /// ===== MEMBER FUNCTIONS =====

    // contains

    fun contains(id: String): Boolean {
        return !config.hasPathOrNull(id)
    }

    // strings

    fun isString(id: String): Boolean {
        return {
            config.getString(id)
        }.doesNotThrow(WRONG_TYPE_EXCEPTION)
    }

    fun getString(id: String, color: Boolean = true): String {
        var string = config.getString(id)
        if (color) string = string.replaceColors()
        return string
    }

    // string lists

    fun isStringList(id: String): Boolean {
        return {
            config.getString(id)
        }.doesNotThrow(WRONG_TYPE_EXCEPTION)
    }

    fun getStringList(id: String, color: Boolean = true): List<String> {
        var list =  config.getStringList(id)
        if (color) list = list.map { it.replaceColors() }
        return list
    }

    // ints

    fun isInt(id: String): Boolean {
        return {
            config.getInt(id)
        }.doesNotThrow(WRONG_TYPE_EXCEPTION)
    }

    fun getInt(id: String): Int {
        return config.getInt(id)
    }

}