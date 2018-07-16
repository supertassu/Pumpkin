package me.tassu.features.punishments.ban

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.punishments.PunishmentManager
import me.tassu.features.punishments.punishment.PunishmentType
import me.tassu.internal.util.kt.toOptional
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.util.ban.Ban
import org.spongepowered.api.util.ban.BanTypes
import java.net.InetAddress
import java.util.*

@Singleton
class PumpkinBanService : BanService {

    @Inject private lateinit var punishmentManager: PunishmentManager

    /**
     * Checks if a [GameProfile] has a ban.
     *
     * @param profile The profile
     * @return True if the profile has a ban, false otherwise
     */
    override fun isBanned(profile: GameProfile): Boolean {
        return punishmentManager
                .getPunishmentsForUser(profile.uniqueId)
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan }
                .any { it.hasNotExpired() }
    }

    /**
     * Checks if an IP has a ban.
     *
     * @param address The address
     * @return True if the address has a ban, false otherwise
     */
    override fun isBanned(address: InetAddress): Boolean {
        return punishmentManager
                .getPunishmentsForIp(address)
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan }
                .any { it.hasNotExpired() }
    }

    /**
     * Adds a ban.
     *
     *
     * If the GameProfile or IP address of the ban already has a ban set,
     * the passed in ban will replace the existing ban.
     *
     * @param ban The ban to add to this ban service
     * @return The previous ban, if available
     *
     * @todo Implement
     */
    override fun addBan(ban: Ban?): Optional<out Ban> {
        return Optional.empty()
    }

    /**
     * Gets all IP bans registered.
     *
     * @return All registered IP bans
     */
    override fun getIpBans(): MutableCollection<Ban.Ip> {
        return punishmentManager
                .getAllPunishments(BanTypes.IP)
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan.Ip }
                .map { it.asSponge() }
                .toMutableSet()
    }

    /**
     * Gets the ban for the given [GameProfile], if available.
     *
     * @param profile The profile
     * @return The ban, if available
     */
    override fun getBanFor(profile: GameProfile): Optional<Ban.Profile> {
        val ban = punishmentManager
                .getPunishmentsForUser(profile.uniqueId)
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan.Uuid }
                .firstOrNull { it.hasNotExpired() } ?: return Optional.empty()

        return ban.asSponge().toOptional()
    }

    /**
     * Gets the ban for the given address, if available.
     *
     * @param address The address.
     * @return All registered IP bans
     */
    override fun getBanFor(address: InetAddress?): Optional<Ban.Ip> {
        val ban = punishmentManager
                .getPunishmentsForIp(address!!)
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan.Ip }
                .firstOrNull { it.hasNotExpired() } ?: return Optional.empty()

        return ban.asSponge().toOptional()
    }

    /**
     * Gets all bans registered.
     *
     * @return All registered bans
     */
    override fun getBans(): MutableCollection<out Ban> {
        return punishmentManager
                .getAllPunishments()
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan }
                .map { it.asSponge() }
                .toMutableSet()
    }

    /**
     * Removes a spongeBan.
     *
     * @param spongeBan The spongeBan
     * @return Whether the spongeBan was present in this spongeBan service
     */
    override fun removeBan(spongeBan: Ban?): Boolean {
        if (spongeBan == null) return false

        val ban = when (spongeBan) {
            is Ban.Profile -> punishmentManager
                    .getPunishmentsForUser(spongeBan.profile.uniqueId)
                    .filter { it.type == PunishmentType.BAN }
                    .map { it as PumpkinBan.Uuid }
                    .firstOrNull { it.hasNotExpired() }
            is Ban.Ip -> punishmentManager
                    .getPunishmentsForIp(spongeBan.address)
                    .filter { it.type == PunishmentType.BAN }
                    .map { it as PumpkinBan.Ip }
                    .firstOrNull { it.hasNotExpired() }
            else -> return false
        } ?: return false

        punishmentManager.revokePunishment(ban)
        return true
    }

    /**
     * Checks if the specified ban is present.
     *
     * @param ban The ban
     * @return True if the ban exists in this ban service, false otherwise
     */
    override fun hasBan(ban: Ban?): Boolean {
        if (ban == null) return false
        if (ban !is PumpkinBan) return false
        return ban.hasNotExpired()
    }

    /**
     * Pardons a profile, or removes its ban, if present.
     *
     * @param profile The profile
     * @return Whether the profile had a ban present
     */
    override fun pardon(profile: GameProfile?): Boolean {
        if (profile == null) return false

        val rawBan = punishmentManager
                .getPunishmentsForUser(profile.uniqueId)
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan.Uuid }
                .firstOrNull { it.hasNotExpired() }
                .toOptional()

        if (!rawBan.isPresent) return false
        val ban = rawBan.get()

        punishmentManager.revokePunishment(ban)

        return true
    }

    /**
     * Pardons an IP address, or removes its ban, if present.
     *
     * @param address The IP address
     * @return Whether the address had a ban present
     */
    override fun pardon(address: InetAddress?): Boolean {
        if (address == null) return false

        val rawBan = punishmentManager
                .getPunishmentsForIp(address)
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan.Uuid }
                .firstOrNull { it.hasNotExpired() }
                .toOptional()

        if (!rawBan.isPresent) return false
        val ban = rawBan.get()

        punishmentManager.revokePunishment(ban)

        return true
    }

    /**
     * Gets all [GameProfile] bans registered.
     *
     * @return All registered [GameProfile] bans
     */
    override fun getProfileBans(): MutableCollection<Ban.Profile> {
        return punishmentManager
                .getAllPunishments(BanTypes.PROFILE)
                .filter { it.type == PunishmentType.BAN }
                .map { it as PumpkinBan.Uuid }
                .map { it.asSponge() }
                .toMutableSet()
    }
}