package me.tassu

import co.aikar.commands.SpongeCommandManager
import com.google.common.collect.ImmutableList
import com.google.inject.Inject
import me.tassu.Pumpkin.DEBUG_RELOAD_CONFIG
import me.tassu.Pumpkin.config
import me.tassu.Pumpkin.container
import me.tassu.Pumpkin.debug
import me.tassu.Pumpkin.log
import me.tassu.Pumpkin.version
import me.tassu.cfg.MainConfig
import me.tassu.cmds.GamemodeCommand
import me.tassu.cmds.completions.GameModeCompletion
import me.tassu.cmds.completions.PlayerCompletion
import me.tassu.util.pop
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.api.Game
import org.spongepowered.api.config.DefaultConfig
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.serializer.TextSerializers
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

/**
 * This is the main class of Pumpkin.
 * @author tassu <git@tassu.me>
 */
@Plugin(id = "pumpkin", name = "Pumpkin")
class PumpkinMain {

    @Inject private lateinit var game: Game

    lateinit var commandManager: SpongeCommandManager

    @Inject
    @DefaultConfig(sharedRoot = false)
    private val configPath: Path? = null

    @Inject
    private fun setContainer(container: PluginContainer) {
        Pumpkin.container = container
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
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

        Pumpkin.textSerializer = TextSerializers.FORMATTING_CODE

        reloadConfig()
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun reload(event: GameReloadEvent) {
        reloadConfig()
    }


    private fun reloadConfig() {
        log.info("")
        log.info("Reloading Pumpkin... ")

        val startTime = System.currentTimeMillis()

        // Save config files & load default configuration
        log.debug("Saving default configuration files.", DEBUG_RELOAD_CONFIG)

        if (Files.notExists(configPath)) {
            container!!.getAsset("pumpkin/pumpkin.conf").get().copyToFile(configPath)
            log.debug("-> Pumpkin.conf was saved to ${configPath!!.toFile().absolutePath}")

            container!!.getAsset("pumpkin/messages.conf").get().copyToFile(configPath)
            log.debug("-> Messages.conf was saved to ${configPath.toFile().absolutePath}")
        }

        log.debug("Reloading configuration from disk.", DEBUG_RELOAD_CONFIG)

        val loader = HoconConfigurationLoader.builder().setPath(configPath).build()
        config = MainConfig(loader)

        // Update debug state
        debug = config.debug
        log.debug("New state for debug is $debug", DEBUG_RELOAD_CONFIG)

        // Register new commands
        log.debug("Registering commands, this might take a while.")
        commandManager = SpongeCommandManager(Pumpkin.container)

        log.debug("-> Registering completions")
        commandManager.commandCompletions.registerCompletion("gamemode") { ImmutableList.of("survival", "creative", "adventure", "spectator") }
        commandManager.commandCompletions.registerCompletion("players") { ctx ->
            game.server.onlinePlayers.stream().filter { if (ctx.issuer.isPlayer) ctx.player.canSee(it) else true }.map { it.name }.toList()
        }

        log.debug("-> Registering contexts")
        commandManager.commandContexts.registerContext(Player::class.java, PlayerCompletion)
        commandManager.commandContexts.registerContext(GameMode::class.java, GameModeCompletion)

        log.debug("-> Loading commands from configuration file.")
        val enabled = config.enabledCommands.map { it.toLowerCase() }.toMutableList()

        while (enabled.isNotEmpty()) {
            val it = enabled.pop().toLowerCase()
            when (it) {
                "gamemode" -> commandManager.registerCommand(GamemodeCommand)
                else -> {
                    log.warn("--> Unknown command: $it")
                }
            }
        }

        if (enabled.isNotEmpty()) {
            log.warn("*** Found ${enabled.size} unknown commands: ${enabled.joinToString()}")
        }

        log.debug("-> All commands registered.")

        log.info("Pumpkin was reloaded in ${System.currentTimeMillis() - startTime} ms.")
    }

}
