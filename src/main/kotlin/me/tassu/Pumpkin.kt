package me.tassu

import me.tassu.cfg.Configuration
import me.tassu.msg.DefaultMessages
import me.tassu.msg.MessageManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

/**
 * This is the main class of Pumpkin.
 * @author tassu <git@tassu.me>
 */
class Pumpkin : JavaPlugin() {

    companion object {
        @get:JvmName("getInstance") val instance: Pumpkin get() {
            return getPlugin(Pumpkin::class.java)
        }

        @get:JvmName("getConfiguration") lateinit var configuration: Configuration
    }

    override fun onEnable() {
        configuration = Configuration(this, "pumpkin")

        MessageManager.init()
        DefaultMessages.reload()
    }

}