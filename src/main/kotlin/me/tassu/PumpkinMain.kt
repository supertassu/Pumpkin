package me.tassu

import com.google.inject.Inject
import com.typesafe.config.ConfigFactory
import me.tassu.Pumpkin.DEBUG_RELOAD_CONFIG
import me.tassu.Pumpkin.container
import me.tassu.Pumpkin.debug
import me.tassu.Pumpkin.log
import me.tassu.Pumpkin.version
import me.tassu.cfg.MainConfig
import me.tassu.cmds.GamemodeCommand
import me.tassu.msg.GeneralMessages
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Files
import java.nio.file.Path

/**
 * This is the main class of Pumpkin.
 * @author tassu <git@tassu.me>
 */
@Plugin(id = "pumpkin", name = "Pumpkin")
class PumpkinMain {

    @Inject
    private fun setContainer(container: PluginContainer) {
        Pumpkin.container = container
    }

    @Inject private lateinit var game: Game

    @Inject
    @ConfigDir(sharedRoot = false)
    private var configDir: Path? = null
    set(value) {
        field = value
        Pumpkin.configDir = value!!
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun serverStarting(event: GameAboutToStartServerEvent) {
        log.info("  ____  __ __ ___  ___ ____  __ __ __ __  __")
        log.info("  || \\\\ || || ||\\\\//|| || \\\\ || // || ||\\ ||")
        log.info("  ||_// || || || \\/ || ||_// ||<<  || ||\\\\||")
        log.info("  ||    \\\\_// ||    || ||    || \\\\ || || \\||")
        log.info("")
        log.info(" -=> LOADING PUMPKIN $version")
        log.info("")
        log.info("Copyright (c) Tassu <pumpkin@tassu.me>.")
        log.info("All rights reserved.")
        log.info("")

        reloadConfig()
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun reload(event: GameReloadEvent) {
        reloadConfig()
    }


    private fun reloadConfig() {
        log.info("Reloading Pumpkin... ")

        val startTime = System.currentTimeMillis()

        // Save config files & load default configuration
        log.debug("Saving default configuration files.", DEBUG_RELOAD_CONFIG)

        val configPath = configDir!!.resolve("pumpkin.conf")
        val messagesPath = configDir!!.resolve("messages.conf")

        val create = Files.notExists(configPath)

        if (create) {
            Files.createDirectories(configDir)
        }

        log.debug("Reloading configuration from disk.", DEBUG_RELOAD_CONFIG)

        MainConfig.reload()
        GeneralMessages.reload()

        // Update debug state
        debug = MainConfig.debug
        log.debug("New state for debug is $debug", DEBUG_RELOAD_CONFIG)

        // Register new commands
        log.debug("Registering commands, this might take a while.", DEBUG_RELOAD_CONFIG)
        log.debug("-> Unregistering old commands", DEBUG_RELOAD_CONFIG)
        Sponge.getCommandManager().getOwnedBy(container!!).forEach {
            Sponge.getCommandManager().removeMapping(it)
        }

        log.debug("-> Loading commands from configuration file.", DEBUG_RELOAD_CONFIG)
        val enabled = MainConfig.enabledCommands.map { it.toLowerCase() }.toMutableList()
        log.debug("--> Found ${enabled.size} commands.", DEBUG_RELOAD_CONFIG)

        while (enabled.isNotEmpty()) {
            val it = enabled.removeAt(0)
            when (it) {
                "gamemode" -> GamemodeCommand.register(container!!)
                else -> {
                    log.warn("*** Unknown command: $it")
                }
            }
        }

        if (enabled.isNotEmpty()) {
            log.warn("** Found ${enabled.size} unknown commands: ${enabled.joinToString()}")
        }

        log.debug("-> All commands registered.", DEBUG_RELOAD_CONFIG)

        log.info("Pumpkin was reloaded in ${System.currentTimeMillis() - startTime} ms.")
    }

}
