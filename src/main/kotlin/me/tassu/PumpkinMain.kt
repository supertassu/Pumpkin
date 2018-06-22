package me.tassu

import co.aikar.commands.SpongeCommandManager
import com.google.common.collect.ImmutableList
import com.google.inject.Inject
import me.tassu.Pumpkin.DEBUG_RELOAD_CONFIG
import me.tassu.Pumpkin.config
import me.tassu.Pumpkin.container
import me.tassu.Pumpkin.debug
import me.tassu.Pumpkin.log
import me.tassu.Pumpkin.messages
import me.tassu.Pumpkin.version
import me.tassu.cfg.MainConfig
import me.tassu.cmds.GamemodeCommand
import me.tassu.cmds.completions.GameModeCompletion
import me.tassu.cmds.completions.PlayerCompletion
import me.tassu.cmds.meta.CommandExceptionHandler
import me.tassu.msg.GeneralMessages
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Files
import java.nio.file.Path
import kotlin.streams.toList

/**
 * This is the main class of Pumpkin.
 * @author tassu <git@tassu.me>
 */
@Plugin(id = "pumpkin", name = "Pumpkin")
class PumpkinMain {

    private lateinit var commandManager: SpongeCommandManager

    @Inject
    private fun setContainer(container: PluginContainer) {
        Pumpkin.container = container
    }

    @Inject private lateinit var game: Game

    @Inject
    @ConfigDir(sharedRoot = false)
    private val configDir: Path? = null

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
        log.info("Copyright (c) Tassu <hello@tassu.me>.")
        log.info("All rights reserved.")

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

        val configPath = configDir!!.resolve("pumpkin.conf")
        val messagesPath = configDir.resolve("messages.conf")

        val create = Files.notExists(configPath)

        if (create) {
            Files.createDirectories(configDir)

            container!!.getAsset("pumpkin.conf").get().copyToFile(configPath)
            log.debug("-> Pumpkin.conf was saved to ${configPath.toFile().absolutePath}", DEBUG_RELOAD_CONFIG)

            container!!.getAsset("messages.conf").get().copyToFile(messagesPath)
            log.debug("-> Messages.conf was saved to ${messagesPath.toFile().absolutePath}", DEBUG_RELOAD_CONFIG)
        }

        log.debug("Reloading configuration from disk.", DEBUG_RELOAD_CONFIG)

        var loader = HoconConfigurationLoader.builder().setPath(configPath).build()
        config = MainConfig(loader)
        if (create) config.save()

        loader = HoconConfigurationLoader.builder().setPath(messagesPath).build()
        messages = GeneralMessages(loader)
        if (create) messages.save()

        // Update debug state
        debug = config.debug
        log.debug("New state for debug is $debug", DEBUG_RELOAD_CONFIG)

        // Register new commands
        log.debug("Registering commands, this might take a while.", DEBUG_RELOAD_CONFIG)
        log.debug("-> Unregistering old commands", DEBUG_RELOAD_CONFIG)
        Sponge.getCommandManager().getOwnedBy(container!!).forEach {
            Sponge.getCommandManager().removeMapping(it)
        }

        commandManager = SpongeCommandManager(Pumpkin.container)

        log.debug("-> Registering completions", DEBUG_RELOAD_CONFIG)
        commandManager.commandCompletions.registerCompletion("gamemode") { ImmutableList.of("survival", "creative", "adventure", "spectator") }
        commandManager.commandCompletions.registerCompletion("players") { ctx ->
            game.server.onlinePlayers.stream().filter { if (ctx.issuer.isPlayer) ctx.player.canSee(it) else true }.map { it.name }.toList()
        }

        log.debug("-> Registering contexts", DEBUG_RELOAD_CONFIG)
        commandManager.commandContexts.registerContext(Player::class.java, PlayerCompletion)
        commandManager.commandContexts.registerContext(GameMode::class.java, GameModeCompletion)

        log.debug("-> Registering error handler", DEBUG_RELOAD_CONFIG)
        commandManager.defaultExceptionHandler = CommandExceptionHandler

        log.debug("-> Loading commands from configuration file.", DEBUG_RELOAD_CONFIG)
        val enabled = config.enabledCommands.map { it.toLowerCase() }.toMutableList()
        log.debug("--> Found ${enabled.size} commands.", DEBUG_RELOAD_CONFIG)

        while (enabled.isNotEmpty()) {
            val it = enabled.removeAt(0)
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

        log.debug("-> All commands registered.", DEBUG_RELOAD_CONFIG)

        log.info("Pumpkin was reloaded in ${System.currentTimeMillis() - startTime} ms.")
    }

}
