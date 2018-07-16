package me.tassu.features.punishments.punishment

import me.tassu.features.punishments.punishment.Punishment
import org.spongepowered.api.util.ban.BanType
import org.spongepowered.api.util.ban.BanTypes
import java.lang.IllegalArgumentException
import java.net.InetAddress
import java.sql.ResultSet
import java.util.*

abstract class AbstractPunishment(resultSet: ResultSet) : Punishment {

    protected val targetType: BanType = if (resultSet.getString("target_type") == "uuid") BanTypes.PROFILE else BanTypes.IP

    init {
        if (resultSet.getString("target_type") != "uuid" && resultSet.getString("target_type") != "ip") {
            throw IllegalArgumentException("FAIL: Target type ${resultSet.getString("target_type")} is not supported.")
        }
    }

    final override val targetUuid: UUID? = if (targetType == BanTypes.PROFILE) UUID.fromString(resultSet.getString("target")) else null
    final override val targetIp: InetAddress? = if (targetType == BanTypes.IP) InetAddress.getByName(resultSet.getString("target")) else null

    init {
        if (targetUuid == null && targetIp == null) {
            throw IllegalArgumentException("FAIL: Both targetUuid and targetIp are null.")
        }
    }

    override val id: Int = resultSet.getInt("id")
    override val actor: UUID = UUID.fromString(resultSet.getString("actor"))
    override val date: Long = resultSet.getLong("unix_timestamp(date)")
    override val expiresOn: Long? = resultSet.getLong("unix_timestamp(expires_on)")
    override val reason: String = resultSet.getString("reason")

    final override val revokedBy: UUID?

    init {
        val string = resultSet.getString("revoked_by")
        revokedBy = if (string != null) {
            UUID.fromString(string)
        } else {
            null
        }
    }

    override val revokedOn: Long? = resultSet.getLong("unix_timestamp(revoked_on)")
    override val revokeReason: String? = resultSet.getString("revoke_reason")

    fun hasExpired(): Boolean {
        if (revokedBy != null) return true
        if (expiresOn == null || expiresOn == 0L) return false
        return expiresOn!! >= System.currentTimeMillis()
    }

    fun hasNotExpired(): Boolean {
        return !hasExpired()
    }


}