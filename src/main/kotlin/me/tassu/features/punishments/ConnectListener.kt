package me.tassu.features.punishments

import com.google.inject.Inject
import com.google.inject.Singleton
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.service.ban.BanService

@Singleton
class ConnectListener {

    @Inject private lateinit var feature: PunishmentFeature

    private val service: BanService get() = feature.banService

    @Listener
    fun onConnect(event: ClientConnectionEvent.Login) {

    }

}