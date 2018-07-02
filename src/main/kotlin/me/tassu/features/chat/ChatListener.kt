package me.tassu.features.chat

import com.google.inject.Inject
import me.tassu.internal.api.prefix.PrefixProvider
import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.feature.Feature
import me.tassu.internal.util.kt.formatColoredMessage
import me.tassu.internal.util.kt.string
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.message.MessageChannelEvent

class ChatListener {

    @Inject private lateinit var module: ChatModule

    internal lateinit var prefixProvider: PrefixProvider
    internal lateinit var format: String

    @Listener
    fun onChat(event: MessageChannelEvent.Chat) {
        if (!module.enabled) return

        val player = event.cause.first(Player::class.java).get()

        val eventFormatter = event.formatter

        val prefix = prefixProvider.providePrefix(player)
        val suffix = prefixProvider.provideSuffix(player)

        val rawMessage = if (eventFormatter.body.isEmpty) event.rawMessage.string() else eventFormatter.body.toText().string()

        event.setMessage(format.formatColoredMessage(
                "user_prefix" to prefix,
                "user_suffix" to suffix,
                "user_name" to player.name,
                "text" to rawMessage.replace("&", "")))
    }

}