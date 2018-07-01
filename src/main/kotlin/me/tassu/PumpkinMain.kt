package me.tassu

import com.google.inject.Inject
import me.tassu.Pumpkin.DEBUG_RELOAD_CONFIG
import me.tassu.Pumpkin.container
import me.tassu.Pumpkin.debug
import me.tassu.Pumpkin.log
import me.tassu.Pumpkin.version
import me.tassu.internal.api.prefix.LuckPermsPrefixProvider
import me.tassu.internal.api.prefix.PrefixProvider
import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.cfg.MainConfig
import me.tassu.cmds.GamemodeCommand
import me.tassu.features.misc.ChatListener
import me.tassu.internal.util.Feature
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Files
import java.nio.file.Path

/**
 * This is the main class of Pumpkin.
 * @author tassu <git@tassu.me>
 */
@Plugin(
        id = "pumpkin",
        name = "Pumpkin",
        dependencies = [
            Dependency(
                    id = "luckperms",
                    optional = true
            )
        ]
)
class PumpkinMain {

    private val features = mapOf<String, Feature>(
            "chat" to ChatListener()
    )

    @Inject
    private fun setContainer(container: PluginContainer) {
        Pumpkin.container = container
    }

    @Inject private lateinit var game: Game

    @Inject
    @ConfigDir(sharedRoot = false)
    private var configDir: Path? = null

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

        Pumpkin.configDir = configDir!!

        Sponge.getEventManager().registerListeners(this, features["chat"]!!)

        // Register dependencies
        log.debug("Registering dependencies", "PumpkinMain#serverStarting()")

        val pluginManager = game.pluginManager

        // Prefix
        if (pluginManager.getPlugin("luckperms").isPresent) {
            log.debug("-> Found dependency \"LuckPerms\" for provider \"Prefix\"", "PumpkinMain#serverStarting()")
            game.serviceManager.setProvider(this, PrefixProvider::class.java, LuckPermsPrefixProvider())
        } else {
            game.serviceManager.setProvider(this, PrefixProvider::class.java, PrefixProvider.DummyPrefixProvider())
        }

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

        if (Files.notExists(configDir)) {
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

        log.debug("Reloading features", DEBUG_RELOAD_CONFIG)

        features.forEach { id, feature ->
            if (MainConfig.enabledFeatures.contains(id)) {
                feature.enable()
            } else {
                feature.disable()
            }
        }

        log.debug("-> Reloaded ${MainConfig.enabledFeatures.size} features", DEBUG_RELOAD_CONFIG)

        log.info("Pumpkin was reloaded in ${System.currentTimeMillis() - startTime} ms.")
    }

}
