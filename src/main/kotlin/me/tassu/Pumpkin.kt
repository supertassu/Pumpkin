package me.tassu

import com.google.inject.Inject
import com.google.inject.Injector
import com.google.inject.Singleton
import me.tassu.internal.api.prefix.LuckPermsPrefixProvider
import me.tassu.internal.api.prefix.PrefixProvider
import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.cfg.MainConfig
import me.tassu.internal.cmds.meta.CommandHolder
import me.tassu.internal.db.DatabaseManager
import me.tassu.internal.di.PumpkinHolder
import me.tassu.internal.feature.FeatureHolder
import me.tassu.internal.util.CacheClearer
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
import java.util.concurrent.TimeUnit

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
    @Inject private lateinit var cacheCleaner: CacheClearer

    private val injector: Injector by lazy {
        PumpkinHolder.getInstance().injector
    }

    val features by lazy {
        return@lazy mapOf(
                "chat" to featureHolder.chat,
                "punishments" to featureHolder.punishment,
                "user_data" to featureHolder.userData,

                // commands
                "cmd_gamemode" to commands.gameModeCommand,
                "cmd_teleport" to commands.teleportCommand,
                "cmd_pumpkin" to commands.pumpkinCommand
        )
    }

    @get:JvmName("isDebuggingEnabled") var debug: Boolean = true

    @get:JvmName("getVersion")
    val version: String get() = PumpkinLoader::class.java.annotations
            .filterIsInstance(Plugin::class.java)
            .first()
            .version

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun serverStarting(event: GameAboutToStartServerEvent) {
        log.info("  §2____  __ __ ___  ___ ____  __ __ __ __  __")
        log.info("  §a|| \\\\ || || ||\\\\//|| || \\\\ || // || ||\\ ||")
        log.info("  §a||_// || || || \\/ || ||_// ||<<  || ||\\\\||")
        log.info("  §a||    \\\\_// ||    || ||    || \\\\ || || \\||")
        log.info("")
        log.info("§a -=> §2STARTING PUMPKIN §a$version")
        log.info("")
        log.info("Copyright (c) Tassu <pumpkin@tassu.me>.")
        log.info("All rights reserved.")
        log.info("")
        val timer = System.currentTimeMillis()

        // Init configurations
        log.info("Initializating configurations")
        mainConfig.init()
        generalMessages.init()

        // Connect to database
        log.info("Connecting to database")

        try {
            databaseManager.connect()
        } catch (e: Exception) {
            log.error("Could not connect to database.", e)
            throw RuntimeException("Database connection failed.", e)
        }

        val ping = databaseManager.ping()

        if (ping.containsKey("Ping")) {
            log.info("-> Ping to database is ${ping["Ping"]}")
        }

        // Register dependencies
        log.info("Registering dependencies")

        val pluginManager = game.pluginManager

        // Prefix
        if (pluginManager.getPlugin("luckperms").isPresent) {
            log.info("-> Found dependency \"LuckPerms\" for provider \"Prefix\"")
            game.serviceManager.setProvider(instance, PrefixProvider::class.java, injector.getInstance(LuckPermsPrefixProvider::class.java))
        } else {
            game.serviceManager.setProvider(instance, PrefixProvider::class.java, injector.getInstance(PrefixProvider.DummyPrefixProvider::class.java))
        }

        // Register features as listeners
        log.info("Registering features")

        val permissionService = game.serviceManager.provideUnchecked(PermissionService::class.java)

        features.forEach { _, it ->
            Sponge.getEventManager().registerListeners(instance, it)
            it.listeners.forEach { listener -> Sponge.getEventManager().registerListeners(instance, listener) }

            it.permissions.forEach { perm ->
                val builder = permissionService.newDescriptionBuilder(instance)

                builder.id("pumpkin.${it.permissionPrefix}.$perm")
                        .description("Permission registered by feature ${it.id}".text())
                        .assign(PermissionDescription.ROLE_STAFF, true)
                        .register()

                log.debug("Registered permission \"pumpkin.${it.permissionPrefix}.$perm\".",
                        "Pumpkin#serverStarting()")
            }
        }

        reloadConfig()

        log.info("§aPumpkin was started in ${System.currentTimeMillis() - timer} ms.")
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun reload(event: GameReloadEvent) {
        val timer = System.currentTimeMillis()
        log.info("§aReloading Pumpkin...")
        reloadConfig()
        log.info("§aPumpkin was reloaded in ${System.currentTimeMillis() - timer} ms.")
    }

    private fun reloadConfig() {
        // Save config files & reload default configuration
        log.info("Saving default configuration files.")

        if (Files.notExists(configDir)) {
            Files.createDirectories(configDir)
        }

        log.info("Reloading configuration from disk.")

        mainConfig.reload()
        generalMessages.reload()

        // Update debug state
        debug = mainConfig.debug
        log.info("New state for debug is $debug")

        // Remove old commands
        log.info("Unregistering old commands")
        Sponge.getCommandManager().getOwnedBy(container).forEach {
            Sponge.getCommandManager().removeMapping(it)
        }

        log.info("Reloading features")

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

        log.info("-> Reloaded ${enabledFeatures.size} features (including dependencies).")

        log.info("Starting CacheCleaner.")
        game.scheduler.createTaskBuilder()
                .async()
                .interval(1, TimeUnit.MINUTES)
                .execute(cacheCleaner)
                .submit(instance)
    }

    @Listener
    @Suppress("UNUSED_PARAMETER")
    fun serverStopping(event: GameStoppedServerEvent) {
        if (databaseManager.isConnected()) {
            log.info("Disconnecting from database... ")
            databaseManager.disconnect()
        }

        log.info("We're done. Bye! ")
    }

}
