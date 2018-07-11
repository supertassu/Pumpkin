package me.tassu

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.api.prefix.LuckPermsPrefixProvider
import me.tassu.internal.api.prefix.PrefixProvider
import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.cfg.MainConfig
import me.tassu.internal.cmds.meta.CommandHolder
import me.tassu.internal.db.DatabaseManager
import me.tassu.internal.feature.FeatureHolder
import me.tassu.internal.util.PumpkinLog
import me.tassu.internal.util.kt.text
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.GameReloadEvent
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent
import org.spongepowered.api.event.game.state.GameStoppedServerEvent
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.service.permission.PermissionDescription
import org.spongepowered.api.service.permission.PermissionService
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

    @Inject private lateinit var databaseManager: DatabaseManager

    // prefix provides
    @Inject private lateinit var emptyPrefixProvider: PrefixProvider.DummyPrefixProvider
    @Inject private lateinit var luckPermsPrefixProvider: LuckPermsPrefixProvider

    private val features by lazy {
        return@lazy mapOf(
                "chat" to featureHolder.chat,
                "punishments" to featureHolder.punishment,
                "user_data" to featureHolder.userData
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
        log.debug("Initializating configurations", "Pumpkin#serverStarting()")
        mainConfig.init()
        generalMessages.init()

        // Connect to database
        log.debug("Connecting to database", "Pumpkin#serverStarting()")

        try {
            databaseManager.connect()
        } catch (e: Exception) {
            log.error("Could not connect to database.", e)
            throw RuntimeException("Database connection failed.", e)
        }

        val ping = databaseManager.ping()

        if (ping.containsKey("Ping")) {
            log.debug("-> Ping to database is ${ping["Ping"]}", "Pumpkin#serverStarting()")
        }

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

        // Register features as listeners
        log.debug("Registering features", "Pumpkin#serverStarting()")

        val permissionService = game.serviceManager.provideUnchecked(PermissionService::class.java)
        val builder = permissionService.newDescriptionBuilder(instance)

        features.forEach { _, it ->
            Sponge.getEventManager().registerListeners(instance, it)
            it.listeners.forEach { listener -> Sponge.getEventManager().registerListeners(instance, listener) }

            it.permissions.forEach { perm ->
                builder.id("pumpkin.feature.${it.id}.$perm")
                        .description("Permission registered by feature ${it.id}".text())
                        .assign(PermissionDescription.ROLE_STAFF, true)
                        .register()
            }
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
        val enabledCommands = mainConfig.enabledCommands.map { it.toLowerCase() }.toMutableList()
        log.debug("--> Found ${enabledCommands.size} commands.", "Pumpkin#reloadConfig()")

        while (enabledCommands.isNotEmpty()) {
            val it = enabledCommands.removeAt(0)
            when (it) {
                "gamemode" -> commands.gameModeCommand.register(container)
                else -> {
                    log.warn("*** Unknown command: $it")
                }
            }
        }

        if (enabledCommands.isNotEmpty()) {
            log.warn("** Found ${enabledCommands.size} unknown commands: ${enabledCommands.joinToString()}")
        }

        log.debug("-> All commands registered.", "Pumpkin#reloadConfig()")

        log.debug("Reloading features", "Pumpkin#reloadConfig()")

        val enabledFeatures = mutableSetOf<String>()

        mainConfig.enabledFeatures.forEach {
            val feature = features[it.toLowerCase()] ?: return@forEach
            enabledFeatures.add(feature.id)
            enabledFeatures.addAll(feature.dependencies)
        }

        features.forEach { id, feature ->
            if (enabledFeatures.contains(id)) {
                feature.enable()
            } else {
                feature.disable()
            }
        }

        log.debug("-> Reloaded ${mainConfig.enabledFeatures.size} features", "Pumpkin#reloadConfig()")

        log.info("Pumpkin was reloaded in ${System.currentTimeMillis() - startTime} ms.")
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun serverStopping(event: GameStoppedServerEvent) {
        if (databaseManager.isConnected()) {
            log.debug("Disconnecting from database... ", "Pumpkin#serverStopping()")
            databaseManager.disconnect()
        }

        log.debug("We're done. Bye! ", "Pumpkin#serverStopping()")
    }

}
