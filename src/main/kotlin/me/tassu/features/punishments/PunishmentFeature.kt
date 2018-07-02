package me.tassu.features.punishments

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.feature.Feature
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.Sponge
import java.util.*

@Singleton
class PunishmentFeature : Feature {

    companion object {
        val CONSOLE_UUID = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
    }

    @Inject
    private lateinit var connectListener: ConnectListener

    private var enabled: Boolean = false

    internal lateinit var banService: BanService

    override val listeners: List<Any> by lazy {
        listOf(connectListener)
    }

    override fun enable() {
        enabled = true
        banService = Sponge.getServiceManager().provide(BanService::class.java).get()
    }

    override fun disable() {
        enabled = false
    }

}