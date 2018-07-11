package me.tassu.features.punishments

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.punishments.ban.PumpkinBanService
import me.tassu.internal.feature.Feature
import me.tassu.internal.util.PumpkinLog
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.Sponge
import org.spongepowered.api.plugin.PluginContainer
import java.util.*

@Singleton
class PunishmentFeature : Feature {

    companion object {
        val CONSOLE_UUID: UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
    }

    @Inject private lateinit var connectListener: ConnectListener
    @Inject private lateinit var container: PluginContainer

    @Inject private lateinit var logger: PumpkinLog

    private var enabled: Boolean = false

    @Inject internal lateinit var banService: BanService

    override val listeners: List<Any> by lazy {
        listOf<Any>(connectListener)
    }

    override fun enable() {
        enabled = true

        Sponge.getServiceManager().setProvider(container, BanService::class.java, banService)
    }

    override fun disable() {
        enabled = false

        if (Sponge.getServiceManager().provide(BanService::class.java).orElse(null) is PumpkinBanService) {
            logger.warn("BanService can not be disabled due to limitations in Sponge API.")
        }
    }

    override val id: String = "punishments"
    override val permissions: List<String> = listOf()
    override val dependencies: List<String> = listOf("user_data")
}