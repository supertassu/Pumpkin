package me.tassu.features.misc

import com.google.inject.Singleton
import me.tassu.internal.feature.SimpleFeature
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent

@Singleton
class NoJoinMessageFeature : SimpleFeature() {
    override val id: String = "disable_join_messages"
    override val listeners: List<Any> = listOf()
    override val permissions: List<String> = listOf()
    override val dependencies: List<String> = listOf()

    @Listener
    fun onJoin(event: ClientConnectionEvent.Join) {
        if (!isEnabled) return
        event.isMessageCancelled = true
    }

    @Listener
    fun onQuit(event: ClientConnectionEvent.Disconnect) {
        if (!isEnabled) return
        event.isMessageCancelled = true
    }

}
