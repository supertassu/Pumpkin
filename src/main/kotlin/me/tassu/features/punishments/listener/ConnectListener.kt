package me.tassu.features.punishments.listener

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.punishments.PunishmentManager
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent

@Singleton
class ConnectListener {

    @Inject private lateinit var manager: PunishmentManager

    @Listener
    fun onConnect(event: ClientConnectionEvent.Login) {

    }

}