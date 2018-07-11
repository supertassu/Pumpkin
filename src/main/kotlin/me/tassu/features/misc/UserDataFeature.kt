package me.tassu.features.misc

import com.google.inject.Singleton
import me.tassu.internal.feature.Feature
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent

@Singleton
class UserDataFeature : Feature {

    override val id: String = "user_data"

    var enabled = false

    override fun enable() {
        enabled = true
    }
    override fun disable() {
        enabled = false
    }

    override val listeners: List<Any> = listOf()
    override val permissions: List<String> = listOf()
    override val dependencies: List<String> = listOf()

    @Listener
    fun onJoin(event: ClientConnectionEvent.Login) {
        val uuid = event.profile.uniqueId
        val name = event.profile.name
        val ip = event.connection.address.address


    }

}