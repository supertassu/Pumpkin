package me.tassu

import co.aikar.commands.BukkitCommandManager
import me.tassu.cfg.Configuration
import me.tassu.cfg.FileConfigurationLoader
import me.tassu.msg.DefaultMessages
import me.tassu.msg.MessageManager
import me.tassu.util.PumpkinLog
import org.bukkit.plugin.java.JavaPlugin

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
        @get:JvmName("logger") val log: PumpkinLog = PumpkinLog
        @get:JvmName("version") val version: String get() = instance.description.version
        @get:JvmName("debug") var debug: Boolean = true

        const val DEBUG_RELOAD_CONFIG = "Pumpkin#reloadConfig()"
    }

    private lateinit var cmdManager: BukkitCommandManager

    override fun onEnable() {
        log.info(" ____  __ __ ___  ___ ____  __ __ __ __  __")
        log.info("  || \\\\ || || ||\\\\//|| || \\\\ || // || ||\\ ||\n")
        log.info("  ||_// || || || \\/ || ||_// ||<<  || ||\\\\||\n")
        log.info("  ||    \\\\_// ||    || ||    || \\\\ || || \\||")
        log.info("")
        log.info(" -=> LOADING PUMPKIN $version")
        log.info("")
        log.info("Copyright (c) Tassu <hello@tassu.me>.")
        log.info("All rights reserved.")

        reloadConfig()
    }

    override fun reloadConfig() {
        log.info("")
        log.info("Reloading Pumpkin... ")

        val startTime = System.currentTimeMillis()

        // Load default configuration
        log.debug("Reloading configuration from disk.", DEBUG_RELOAD_CONFIG)
        configuration = FileConfigurationLoader.load("pumpkin")

        // Update debug state
        debug = configuration.getBoolean("debug")
        log.debug("New state for debug is $debug", DEBUG_RELOAD_CONFIG)

        // Load messages from configuration
        log.debug("Reloading messages.", DEBUG_RELOAD_CONFIG)
        MessageManager.init()
        DefaultMessages.reload()

        // Clean up old commands.
        if (::cmdManager.isInitialized) {
            log.debug("Un-registering old commands.", DEBUG_RELOAD_CONFIG)
            val commandStartTime = System.currentTimeMillis()

            cmdManager.unregisterCommands()

            log.debug("Old commands removed in ${System.currentTimeMillis() - commandStartTime} ms.", DEBUG_RELOAD_CONFIG)
        }

        // Register new commands
        log.debug("Registering new commands.")

        cmdManager = BukkitCommandManager(this)

        log.info("Pumpkin was reloaded in ${System.currentTimeMillis() - startTime} ms.")
    }

}
