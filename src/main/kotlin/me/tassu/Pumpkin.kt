package me.tassu

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.api.prefix.LuckPermsPrefixProvider
import me.tassu.internal.api.prefix.PrefixProvider
import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.cfg.MainConfig
import me.tassu.internal.cmds.meta.CommandHolder
import me.tassu.internal.feature.Feature
import me.tassu.internal.feature.FeatureHolder
import me.tassu.internal.util.PumpkinLog
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
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
@Singleton
class Pumpkin {

    @Inject private lateinit var game: Game
    @Inject private lateinit var log: PumpkinLog
    @Inject private lateinit var container: PluginContainer
    @Inject private lateinit var configDir: Path

    private val instance by lazy { container.instance.get() as PumpkinLoader }

    @Inject private lateinit var mainConfig: MainConfig
    @Inject private lateinit var generalMessages: GeneralMessages

    @Inject private lateinit var commands: CommandHolder
    @Inject private lateinit var featureHolder: FeatureHolder

    // prefix provides
    @Inject private lateinit var emptyPrefixProvider: PrefixProvider.DummyPrefixProvider
    @Inject private lateinit var luckPermsPrefixProvider: LuckPermsPrefixProvider

    private val features by lazy {
        return@lazy mapOf<String, Feature>(
                "chat" to featureHolder.chat
        )
    }

    @get:JvmName("isDebugEnabled") var debug: Boolean = true

    @get:JvmName("getVersion")
    private val version: String get() = PumpkinLoader::class.java.annotations
            .filterIsInstance(Plugin::class.java)
            .first()
            .version
    
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

        // Init configurations
        mainConfig.init()
        generalMessages.init()

        // Register dependencies
        log.debug("Registering dependencies", "Pumpkin#serverStarting()")

        val pluginManager = game.pluginManager

        // Prefix
        if (pluginManager.getPlugin("luckperms").isPresent) {
            log.debug("-> Found dependency \"LuckPerms\" for provider \"Prefix\"", "Pumpkin#serverStarting()")
            game.serviceManager.setProvider(instance, PrefixProvider::class.java, luckPermsPrefixProvider)
        } else {
            game.serviceManager.setProvider(instance, PrefixProvider::class.java, emptyPrefixProvider)
        }

        // Register features as commands
        log.debug("Registering features", "Pumpkin#serverStarting()")

        features.forEach { _, it ->
            Sponge.getEventManager().registerListeners(instance, it)
            it.listeners.forEach { listener -> Sponge.getEventManager().registerListeners(instance, listener) }
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

        // Save config files & reload default configuration
        log.debug("Saving default configuration files.", "Pumpkin#reloadConfig()")

        if (Files.notExists(configDir)) {
            Files.createDirectories(configDir)
        }

        log.debug("Reloading configuration from disk.", "Pumpkin#reloadConfig()")

        mainConfig.reload()
        generalMessages.reload()

        // Update debug state
        debug = mainConfig.debug
        log.debug("New state for debug is $debug", "Pumpkin#reloadConfig()")

        // Register new commands
        log.debug("Registering commands, this might take a while.", "Pumpkin#reloadConfig()")
        log.debug("-> Unregistering old commands", "Pumpkin#reloadConfig()")
        Sponge.getCommandManager().getOwnedBy(container).forEach {
            Sponge.getCommandManager().removeMapping(it)
        }

        log.debug("-> Loading commands from configuration file.", "Pumpkin#reloadConfig()")
        val enabled = mainConfig.enabledCommands.map { it.toLowerCase() }.toMutableList()
        log.debug("--> Found ${enabled.size} commands.", "Pumpkin#reloadConfig()")

        while (enabled.isNotEmpty()) {
            val it = enabled.removeAt(0)
            when (it) {
                "gamemode" -> commands.gameModeCommand.register(container)
                else -> {
                    log.warn("*** Unknown command: $it")
                }
            }
        }

        if (enabled.isNotEmpty()) {
            log.warn("** Found ${enabled.size} unknown commands: ${enabled.joinToString()}")
        }

        log.debug("-> All commands registered.", "Pumpkin#reloadConfig()")

        log.debug("Reloading features", "Pumpkin#reloadConfig()")

        features.forEach { id, feature ->
            if (mainConfig.enabledFeatures.contains(id)) {
                feature.enable()
            } else {
                feature.disable()
            }
        }

        log.debug("-> Reloaded ${mainConfig.enabledFeatures.size} features", "Pumpkin#reloadConfig()")

        log.info("Pumpkin was reloaded in ${System.currentTimeMillis() - startTime} ms.")
    }

}
