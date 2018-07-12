package me.tassu.features.punishments.ban

import me.tassu.features.punishments.AbstractPunishment
import me.tassu.features.punishments.PunishmentFeature
import me.tassu.features.punishments.PunishmentType
import me.tassu.internal.util.kt.text
import me.tassu.internal.util.kt.toOptional
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.user.UserStorageService
import org.spongepowered.api.text.Text
import org.spongepowered.api.util.ban.Ban
import org.spongepowered.api.util.ban.BanType
import org.spongepowered.api.util.ban.BanTypes
import java.net.InetAddress
import java.sql.ResultSet
import java.time.Instant
import java.util.*

abstract class PumpkinBan(data: ResultSet) : AbstractPunishment(data), Ban {

    override val type: PunishmentType = PunishmentType.BAN

    override fun getType(): BanType {
        return targetType
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
        val player = Sponge.getServer().getPlayer(this.actor)
        return (player as CommandSource).toOptional()
    }

    override fun getReason(): Optional<Text> {
        return reason.text().toOptional()
    }

    init {
        if (data.getString("type") != "ban") {
            throw IllegalArgumentException("FAIL: Punishment type ${data.getString("type")} is not a ban.")
        }
    }

    class Ip(resultSet: ResultSet) : PumpkinBan(resultSet), Ban.Ip {
        init {
            if (this.targetType != BanTypes.IP) {
                throw IllegalArgumentException("FAIL: Invalid target type $targetType, expected IP.")
            }
        }

        override fun getAddress(): InetAddress {
            return targetIp!!
        }
    }

    class Uuid(resultSet: ResultSet) : PumpkinBan(resultSet), Ban.Profile {
        init {
            if (this.targetType != BanTypes.PROFILE) {
                throw IllegalArgumentException("FAIL: Invalid target type $targetType, expected PROFILE.")
            }
        }

        override fun getProfile(): GameProfile {
            return GameProfile.of(targetUuid!!)
        }
    }

}