package me.tassu.features.misc

import me.tassu.internal.api.prefix.PrefixProvider
import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.util.Feature
import me.tassu.internal.util.formatColoredMessage
import me.tassu.internal.util.string
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.message.MessageChannelEvent


class ChatListener : Feature {

    var enabled: Boolean = false

    lateinit var prefixProvider: PrefixProvider
    lateinit var format: String

    override fun enable() {
        enabled = true
        prefixProvider = Sponge.getServiceManager().provideUnchecked(PrefixProvider::class.java)
        format = GeneralMessages.chatFormat
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
                "text" to rawMessage.replace("&", "")))
    }

}