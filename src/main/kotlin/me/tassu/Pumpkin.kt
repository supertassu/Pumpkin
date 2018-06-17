package me.tassu

import co.aikar.commands.SpongeCommandManager
import com.google.inject.Inject
import com.uchuhimo.konf.Config
import me.tassu.cfg.DefaultConfig
import me.tassu.cfg.FileManager
import me.tassu.msg.CommandMessages
import me.tassu.msg.GeneralMessages
import me.tassu.util.PumpkinLog
import org.spongepowered.api.Game
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Path
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.game.GameReloadEvent

/**
 * This is the main class of Pumpkin.
 * @author tassu <git@tassu.me>
 */
@Plugin(id = "pumpkin", name = "Pumpkin")
class Pumpkin {

    companion object {
        @get:JvmName("logger") val log: PumpkinLog = PumpkinLog
        @get:JvmName("debug") var debug: Boolean = true
        @get:JvmName("version") val version: String get() = Pumpkin::class.java.annotations
                .filterIsInstance(Plugin::class.java)
                .first()
                .version

        @get:JvmName("container") lateinit var container: PluginContainer
        @get:JvmName("config") lateinit var config: Config

        const val DEBUG_RELOAD_CONFIG = "Pumpkin#reloadConfig()"
    }

    @Inject private lateinit var game: Game
    @Inject private val pluginContainer: PluginContainer? = null

    @Inject
    private fun setContainer(container: PluginContainer) {
        Pumpkin.container = container
    }

    @Inject
    @ConfigDir(sharedRoot = false)
    val configDir: Path? = null

    @Listener
    fun serverStarting(event: GameAboutToStartServerEvent) {
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

    @Listener
    fun reload(event: GameReloadEvent) {
        reloadConfig()
    }


    private fun reloadConfig() {
        log.info("")
        log.info("Reloading Pumpkin... ")

        val startTime = System.currentTimeMillis()

        // Save config files & load default configuration
        log.debug("Saving default configuration files", DEBUG_RELOAD_CONFIG)
        FileManager.handle("pumpkin.conf", "messages.conf", "itemdb.conf")

        log.debug("Reloading configuration from disk.", DEBUG_RELOAD_CONFIG)

        config = Config {
            addSpec(DefaultConfig)
            addSpec(GeneralMessages)
            addSpec(CommandMessages)
        }
                .withSourceFrom.file( FileManager.file("pumpkin.conf") )
                .withSourceFrom.file( FileManager.file("messages.conf") )
                .withSourceFrom.file( FileManager.file("itemdb.conf") )

        // Update debug state
        debug = config[DefaultConfig.debug]
        log.debug("New state for debug is $debug", DEBUG_RELOAD_CONFIG)

        // Register new commands
        log.debug("Registering commands.")


        log.info("Pumpkin was reloaded in ${System.currentTimeMillis() - startTime} ms.")
    }

}
