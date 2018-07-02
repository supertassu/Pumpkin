package me.tassu.features.punishments.ban

import me.tassu.features.punishments.AbstractPunishment
import me.tassu.features.punishments.PunishmentFeature
import me.tassu.features.punishments.PunishmentType
import me.tassu.internal.util.kt.text
import me.tassu.internal.util.kt.toOptional
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.util.ban.Ban
import org.spongepowered.api.util.ban.BanType
import org.spongepowered.api.util.ban.BanTypes
import java.sql.ResultSet
import java.time.Instant
import java.util.*

class PumpkinBan(resultSet: ResultSet) : AbstractPunishment(resultSet), Ban {

    override val type: PunishmentType = PunishmentType.BAN

    override fun getType(): BanType {
        return BanTypes.PROFILE
    }

    override fun getBanSource(): Optional<Text> {
        val text = banCommandSource
        if (text.isPresent) return text.get().name.text().toOptional()

        val user = Sponge.getServiceManager().provide(UserStorageService::class.java).get().get(this.actor)
        if (!user.isPresent) return Optional.empty()
        return user.get().name.text().toOptional()
    }

    override fun getCreationDate(): Instant {
        return Instant.ofEpochSecond(this.date)
    }

    override fun getExpirationDate(): Optional<Instant> {
        if (this.expiresOn == null) return Optional.empty()
        return  Instant.ofEpochSecond(this.expiresOn!!).toOptional()
    }

    override fun getBanCommandSource(): Optional<CommandSource> {
        if (this.actor == PunishmentFeature.CONSOLE_UUID) return Sponge.getServer().console.toOptional()
        val player = Sponge.getServer().getPlayer(this.actor).orElse(null)
        return player.toOptional()
    }

    override fun getReason(): Optional<Text> {
        return reason.text().toOptional()
    }

    init {
        if (resultSet.getString("type") != "ban") {
            throw IllegalArgumentException("FAIL: Punishment type ${resultSet.getString("type")} is not a ban.")
        }
    }

}