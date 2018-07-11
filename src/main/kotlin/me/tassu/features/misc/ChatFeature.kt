package me.tassu.features.misc

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.api.prefix.PrefixProvider
import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.feature.Feature
import me.tassu.internal.util.kt.formatColoredMessage
import me.tassu.internal.util.kt.string
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.message.MessageChannelEvent

@Singleton
class ChatFeature : Feature {

    override val id: String = "chat"

    private lateinit var prefixProvider: PrefixProvider
    private lateinit var format: String
    private var enabled: Boolean = false

    @Inject private lateinit var generalMessages: GeneralMessages

    override fun enable() {
        enabled = true
        prefixProvider = Sponge.getServiceManager().provideUnchecked(PrefixProvider::class.java)
        format = generalMessages.chat.format
    }

    override fun disable() {
        enabled = false
    }

    @Listener
    fun onChat(event: MessageChannelEvent.Chat) {
        if (!enabled) return

        val player = event.cause.first(Player::class.java).get()

        val eventFormatter = event.formatter

        val prefix = prefixProvider.providePrefix(player)
        val suffix = prefixProvider.provideSuffix(player)

        val rawMessage = if (eventFormatter.body.isEmpty) event.rawMessage.string() else eventFormatter.body.toText().string()

        event.setMessage(format.formatColoredMessage(
                "user_prefix" to prefix,
                "user_suffix" to suffix,
                "user_name" to player.name,
                "text" to rawMessage.replace("&", "&\\")))
    }

    override val listeners: List<Any> = listOf()
    override val permissions: List<String> = listOf("color")
    override val dependencies: List<String> = listOf()
}