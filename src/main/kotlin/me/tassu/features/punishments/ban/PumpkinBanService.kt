package me.tassu.features.punishments.ban

import com.google.inject.Singleton
import org.spongepowered.api.profile.GameProfile
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.util.ban.Ban
import java.net.InetAddress
import java.util.*

@Singleton
class PumpkinBanService : BanService {

    /**
     * Checks if a [GameProfile] has a ban.
     *
     * @param profile The profile
     * @return True if the profile has a ban, false otherwise
     */
    override fun isBanned(profile: GameProfile?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Checks if an IP has a ban.
     *
     * @param address The address
     * @return True if the address has a ban, false otherwise
     */
    override fun isBanned(address: InetAddress?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
     */
    override fun addBan(ban: Ban?): Optional<out Ban> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Gets all IP bans registered.
     *
     * @return All registered IP bans
     */
    override fun getIpBans(): MutableCollection<Ban.Ip> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Gets the ban for the given [GameProfile], if available.
     *
     * @param profile The profile
     * @return The ban, if available
     */
    override fun getBanFor(profile: GameProfile?): Optional<Ban.Profile> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Gets the ban for the given address, if available.
     *
     * @param address The address.
     * @return All registered IP bans
     */
    override fun getBanFor(address: InetAddress?): Optional<Ban.Ip> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Gets all bans registered.
     *
     * @return All registered bans
     */
    override fun getBans(): MutableCollection<out Ban> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Removes a ban.
     *
     * @param ban The ban
     * @return Whether the ban was present in this ban service
     */
    override fun removeBan(ban: Ban?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Pardons an IP address, or removes its ban, if present.
     *
     * @param address The IP address
     * @return Whether the address had a ban present
     */
    override fun pardon(address: InetAddress?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Gets all [GameProfile] bans registered.
     *
     * @return All registered [GameProfile] bans
     */
    override fun getProfileBans(): MutableCollection<Ban.Profile> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}