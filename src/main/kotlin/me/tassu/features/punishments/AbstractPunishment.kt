package me.tassu.features.punishments

import java.sql.ResultSet
import java.util.*

abstract class AbstractPunishment(resultSet: ResultSet) : Punishment {

    init {
        if (resultSet.getString("target_type") != "uuid") {
            throw IllegalArgumentException("FAIL: Target type ${resultSet.getString("target_type")} is not supported.")
        }
    }

    override val target: UUID by lazy { UUID.fromString(resultSet.getString("target")) }
    override val actor: UUID by lazy { UUID.fromString(resultSet.getString("actor")) }
    override val date: Long by lazy { resultSet.getLong("date") }
    override val expiresOn: Long? by lazy { resultSet.getLong("expires_on") }

    override val reason: String by lazy { resultSet.getString("reason") }
    override val revokedBy: UUID? by lazy {
        val string = resultSet.getString("target") ?: return@lazy null
        return@lazy UUID.fromString(string)
    }

    override val revokedOn: Long? by lazy { resultSet.getLong("revoked_on") }
    override val revokeReason: String? by lazy { resultSet.getString("revoke_reason") }

    fun hasExpired(): Boolean {
        if (revokedBy != null) return true
        if (expiresOn == null) return false
        return expiresOn!! >= System.currentTimeMillis()
    }

    fun hasNotExpired(): Boolean {
        return !hasExpired()
    }


}