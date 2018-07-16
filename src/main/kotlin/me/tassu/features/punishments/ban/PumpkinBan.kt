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

abstract class PumpkinBan(data: ResultSet) : AbstractPunishment(data) {

    abstract fun asSponge(): Ban

    override val type: PunishmentType = PunishmentType.BAN

    fun getType(): BanType {
        return targetType
    }

    fun getBanSource(): Optional<Text> {
        val text = getBanCommandSource()
        if (text.isPresent) return text.get().name.text().toOptional()

        val user = Sponge.getServiceManager().provide(UserStorageService::class.java).get().get(this.actor)
        if (!user.isPresent) return Optional.empty()
        return user.get().name.text().toOptional()
    }

    fun getCreationDate(): Instant {
        return Instant.ofEpochSecond(this.date)
    }

    fun getExpirationDate(): Optional<Instant> {
        if (this.expiresOn == null) return Optional.empty()
        return  Instant.ofEpochSecond(this.expiresOn!!).toOptional()
    }

    fun getBanCommandSource(): Optional<CommandSource> {
        if (this.actor == PunishmentFeature.CONSOLE_UUID) return Sponge.getServer().console.toOptional()
        val player = Sponge.getServer().getPlayer(this.actor)
        return (player as CommandSource).toOptional()
    }

    fun getReason(): Optional<Text> {
        return reason.text().toOptional()
    }

    init {
        if (data.getString("type") != "ban") {
            throw IllegalArgumentException("FAIL: Punishment type ${data.getString("type")} is not a ban.")
        }
    }

    class Ip(resultSet: ResultSet) : PumpkinBan(resultSet) {
        init {
            if (this.targetType != BanTypes.IP) {
                throw IllegalArgumentException("FAIL: Invalid target type $targetType, expected IP.")
            }
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun getAddress(): InetAddress {
            return targetIp!!
        }

        override fun asSponge(): Ban.Ip {
            var builder = Ban.builder()
                    .type(BanTypes.IP)
                    .address(getAddress())
                    .reason(getReason().orElse("No reason provided.".text()))
                    .startDate(getCreationDate())

            builder = if (getBanCommandSource().isPresent) {
                builder.source(getBanCommandSource().get())
            } else {
                builder.source(getBanSource().orElse("Unknown source.".text()))
            }

            if (getExpirationDate().isPresent) {
                builder = builder.expirationDate(getExpirationDate().get())
            }

            return builder.build() as Ban.Ip
        }
    }

    class Uuid(resultSet: ResultSet) : PumpkinBan(resultSet) {
        init {
            if (this.targetType != BanTypes.PROFILE) {
                throw IllegalArgumentException("FAIL: Invalid target type $targetType, expected PROFILE.")
            }
        }

        @Suppress("MemberVisibilityCanBePrivate")
        fun getProfile(): GameProfile {
            return GameProfile.of(targetUuid!!)
        }

        override fun asSponge(): Ban.Profile {
            var builder = Ban.builder()
                    .type(BanTypes.PROFILE)
                    .profile(getProfile())
                    .reason(getReason().orElse("No reason provided.".text()))
                    .startDate(getCreationDate())

            builder = if (getBanCommandSource().isPresent) {
                builder.source(getBanCommandSource().get())
            } else {
                builder.source(getBanSource().orElse("Unknown source.".text()))
            }

            if (getExpirationDate().isPresent) {
                builder = builder.expirationDate(getExpirationDate().get())
            }

            return builder.build() as Ban.Profile
        }
    }

}