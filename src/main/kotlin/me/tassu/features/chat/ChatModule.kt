package me.tassu.features.chat

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.api.prefix.PrefixProvider
import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.feature.Feature
import org.spongepowered.api.Sponge

@Singleton
class ChatModule : Feature {

    @Inject private lateinit var listener: ChatListener

    @Inject private lateinit var generalMessages: GeneralMessages

    override fun enable() {
        enabled = true
        listener.prefixProvider = Sponge.getServiceManager().provideUnchecked(PrefixProvider::class.java)
        listener.format = generalMessages.chat.format
    }

    override fun disable() {
        enabled = false
    }

    override val listeners: List<Any> by lazy {
        return@lazy listOf<Any>(listener)
    }

    var enabled: Boolean = false

}