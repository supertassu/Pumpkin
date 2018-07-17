package me.tassu.features.punishments.listener

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.punishments.PunishmentFeature
import me.tassu.features.punishments.PunishmentManager
import me.tassu.features.punishments.ban.PumpkinBan
import me.tassu.features.punishments.punishment.PunishmentType
import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.util.kt.formatColoredMessage
import me.tassu.internal.util.kt.text
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.Order
import org.spongepowered.api.event.filter.IsCancelled
import org.spongepowered.api.event.network.ClientConnectionEvent
import org.spongepowered.api.util.Tristate

@Singleton
class ConnectListener {

    @Inject private lateinit var manager: PunishmentManager
    @Inject private lateinit var feature: PunishmentFeature
    @Inject private lateinit var generalMessages: GeneralMessages

    @Listener(beforeModifications = true, order = Order.FIRST)
    @IsCancelled(Tristate.UNDEFINED)
    fun onConnect(event: ClientConnectionEvent.Login) {
        if (!feature.isEnabled) return

        val ban = manager
                .getPunishmentsForUser(event.profile.uniqueId)
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan.Uuid }
                .firstOrNull { it.hasNotExpired() } ?: return

        event.isCancelled = true
        event.setMessage(generalMessages.punishments.permaBannedMsg.formatColoredMessage(
                "actor" to ban.getBanSource().orElse("Unknown user".text()),
                "reason" to ban.getReason().orElse("No reason specified".text())))
    }

}