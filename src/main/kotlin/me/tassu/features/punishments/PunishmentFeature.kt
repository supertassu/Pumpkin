package me.tassu.features.punishments

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.punishments.ban.PumpkinBanService
import me.tassu.features.punishments.cmd.BanCommand
import me.tassu.features.punishments.cmd.PardonCommand
import me.tassu.features.punishments.listener.ConnectListener
import me.tassu.internal.cmds.meta.AbstractCommand
import me.tassu.internal.feature.Feature
import me.tassu.internal.util.PumpkinLog
import org.spongepowered.api.Sponge
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.service.ban.BanService
import java.util.*

@Singleton
class PunishmentFeature : Feature {

    companion object {
        val CONSOLE_UUID: UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
    }

    @Inject
    private lateinit var connectListener: ConnectListener
    @Inject
    private lateinit var container: PluginContainer

    @Inject
    private lateinit var logger: PumpkinLog

    private var enabled: Boolean = false

    @Inject
    private lateinit var banService: BanService

    override val listeners: List<Any> by lazy {
        listOf<Any>(connectListener)
    }

    @Inject private lateinit var banCommand: BanCommand
    @Inject private lateinit var pardonCommand: PardonCommand

    private val commands: List<AbstractCommand> by lazy {
        listOf(
                banCommand,
                pardonCommand
        )
    }

    override fun enable() {
        enabled = true

        commands.forEach {
            it.enable()
        }

        Sponge.getServiceManager().setProvider(container, BanService::class.java, banService)
    }

    override fun disable() {
        if (Sponge.getServiceManager().provide(BanService::class.java).orElse(null) is PumpkinBanService) {
            logger.warn("PunishmentFeature can not be disabled due to limitations in Sponge API.")
            logger.warn("Please re-start the server to disable it.")
        } else {
            commands.forEach {
                it.disable()
            }

            enabled = false
        }
    }

    override val isEnabled: Boolean
        get() = enabled

    override val id: String = "punishments"

    override val permissions: List<String> by lazy {
        val list = mutableListOf<String>()

        commands.forEach {
            it.permissions.forEach { perm ->
                list.add("commands.${it.name}." + perm)
            }
        }

        list
    }

    override val dependencies: List<String> = listOf("user_data")
    override val permissionPrefix: String
        get() = "feature.$id"
}