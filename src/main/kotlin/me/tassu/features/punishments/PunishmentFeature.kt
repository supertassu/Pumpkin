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

    @Inject private lateinit var banService: BanService

    override val listeners: List<Any> by lazy {
        listOf<Any>(connectListener)
    }

    override fun enable() {
        enabled = true

        logger.warn("BanService can not be enabled due to a bug in SpongeCommon.")
        logger.warn("See: https://github.com/SpongePowered/SpongeCommon/issues/1990")
        logger.warn("Other plugins can not create PumpkinBans, however the bans")
        logger.warn("* created by any Pumpkin ways (commands etc) will work just fine.")
        //Sponge.getServiceManager().setProvider(container, BanService::class.java, banService)
    }

    override fun disable() {
        if (Sponge.getServiceManager().provide(BanService::class.java).orElse(null) is PumpkinBanService) {
            logger.warn("PunishmentFeature can not be disabled due to limitations in Sponge API.")
            logger.warn("Please re-start the server to disable it.")
        } else {
            enabled = false
        }
    }

    override val id: String = "punishments"
    override val permissions: List<String> = listOf()
    override val dependencies: List<String> = listOf("user_data")
}