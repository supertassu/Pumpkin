package me.tassu

import me.tassu.cfg.MainConfig
import me.tassu.msg.GeneralMessages
import me.tassu.util.PumpkinLog
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.serializer.TextSerializer

object Pumpkin {

    @get:JvmName("logger") val log: PumpkinLog = PumpkinLog
    @get:JvmName("debug") var debug: Boolean = true
    @get:JvmName("version") val version: String get() = PumpkinMain::class.java.annotations
            .filterIsInstance(Plugin::class.java)
            .first()
            .version

    @get:JvmName("container") var container: PluginContainer? = null

    @get:JvmName("config") lateinit var config: MainConfig
    @get:JvmName("messages") lateinit var messages: GeneralMessages

    const val DEBUG_RELOAD_CONFIG = "PumpkinMain#reloadConfig()"

    @get:JvmName("textSerializer") lateinit var textSerializer: TextSerializer

}