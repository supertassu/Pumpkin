package me.tassu.features.punishments

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.feature.Feature
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.Sponge



@Singleton
class PunishmentFeature : Feature {

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