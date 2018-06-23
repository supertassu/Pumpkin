package me.tassu

import me.tassu.util.PumpkinLog
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Path

object Pumpkin {

    @get:JvmName("logger") val log: PumpkinLog = PumpkinLog
    @get:JvmName("debug") var debug: Boolean = true
    @get:JvmName("version") val version: String get() = PumpkinMain::class.java.annotations
            .filterIsInstance(Plugin::class.java)
            .first()
            .version

    @get:JvmName("container") var container: PluginContainer? = null

    const val DEBUG_RELOAD_CONFIG = "PumpkinMain#reloadConfig()"

    lateinit var configDir: Path

}