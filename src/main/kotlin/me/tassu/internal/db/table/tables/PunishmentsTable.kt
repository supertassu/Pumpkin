package me.tassu.internal.db.table.tables

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.punishments.Punishment
import me.tassu.features.punishments.ban.PumpkinBan
import me.tassu.internal.db.DatabaseManager
import me.tassu.internal.db.table.AbstractTable
import org.spongepowered.api.util.ban.BanType
import org.spongepowered.api.util.ban.BanTypes
import java.net.InetAddress
import java.sql.Connection
import java.sql.ResultSet
import java.util.*
import java.util.function.Function

@Singleton
class PunishmentsTable : AbstractTable() {

    companion object {
        private const val CREATE_TABLE_SCHEMA = "CREATE TABLE %NAME (" +
                "`id` INT PRIMARY KEY AUTO_INCREMENT NOT NULL, " +
                "`type` ENUM('ban', 'kick', 'mute'), " +
                "`target_type` ENUM('uuid', 'ip'), " +
                "`target` VARCHAR(36) NOT NULL, " +
                "`actor` VARCHAR(36) NOT NULL, " +
                "`date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "`expires_on` TIMESTAMP, " +
                "`reason` TEXT NOT NULL, " +
                "`revoked_on` TIMESTAMP, " +
                "`revoked_by` VARCHAR(36), " +
                "`revoke_reason` TEXT, " +
                "`flags` TEXT NOT NULL);"

        private const val SELECT_VARIABLES = "id, type, target_type, target, actor, unix_timestamp(date), unix_timestamp(expires_on), " +
                "reason, unix_timestamp(revoked_on), revoked_by, revoke_reason, flags"

        private const val QUERY_BY_ID_SCHEMA = "SELECT $SELECT_VARIABLES FROM %NAME WHERE id=?;"

        private const val QUERY_BY_TARGET_UUID_SCHEMA = "SELECT $SELECT_VARIABLES FROM %NAME WHERE target=? AND target_type='uuid';"
        private const val QUERY_BY_TARGET_UUID_FROM_NAME_SCHEMA = "SELECT $SELECT_VARIABLES FROM %NAME WHERE target=(SELECT uuid FROM %USER_CACHE_NAME WHERE name=?) AND target_type='uuid';"

        private const val QUERY_BY_TARGET_IP_SCHEMA = "SELECT $SELECT_VARIABLES FROM %NAME WHERE target=? AND target_type='ip';"

        private const val QUERY_EVERYTHING_SCHEMA = "SELECT $SELECT_VARIABLES FROM %NAME;"

        private const val QUERY_EVERYTHING_BY_TYPE_SCHEMA = "SELECT $SELECT_VARIABLES FROM %NAME WHERE target_type='?';"
    }

    private val nameReplacer = Function<String, String> {
        it.replace("%NAME", getName()).replace("%USER_CACHE_NAME", databaseManager.tablePrefix + "users")
    }

    @Inject private lateinit var databaseManager: DatabaseManager

    override fun getName(): String {
        return databaseManager.tablePrefix + "punishments"
    }

    override fun getCurrentVersion(): Int {
        return 1
    }

    private fun getFromResultSet(result: ResultSet): Punishment {
        return when (result.getString("type")) {
            "ban" -> {
                if (result.getString("target_type") == "uuid") {
                    PumpkinBan.Uuid(result)
                } else {
                    PumpkinBan.Ip(result)
                }
            }
            else -> {
                throw IllegalArgumentException("FAIL: Punishment type ${result.getString("type")} is not supported.")
            }
        }
    }

    override fun migrateFromOlderVersion(olderVersion: Int, connection: Connection): Int {
        if (olderVersion == getCurrentVersion()) {
            return getCurrentVersion()
        }

        connection.createStatement().use { s ->
            s.execute(nameReplacer.apply(CREATE_TABLE_SCHEMA))
        }

        return getCurrentVersion()
    }

    fun queryById(id: Int): Set<Punishment> {
        return databaseManager.getConnection().use {
            return@use it.prepareStatement(nameReplacer.apply(QUERY_BY_ID_SCHEMA)).use { s ->
                s.setInt(1, id)

                val result = s.executeQuery()
                val set = mutableSetOf<Punishment>()

                while (result.next()) {
                    set.add(getFromResultSet(result))
                }

                result.close()
                set
            }
        }
    }

    fun queryByTargetUuid(uuid: UUID): Set<Punishment> {
        return databaseManager.getConnection().use {
            return@use it.prepareStatement(nameReplacer.apply(QUERY_BY_TARGET_UUID_SCHEMA)).use { s ->
                s.setString(1, uuid.toString())

                val result = s.executeQuery()
                val set = mutableSetOf<Punishment>()

                while (result.next()) {
                    set.add(getFromResultSet(result))
                }

                result.close()
                set
            }
        }
    }

    fun queryByTargetName(name: String): Set<Punishment> {
        return databaseManager.getConnection().use {
            return@use it.prepareStatement(nameReplacer.apply(QUERY_BY_TARGET_UUID_FROM_NAME_SCHEMA)).use { s ->
                s.setString(1, name)

                val result = s.executeQuery()
                val set = mutableSetOf<Punishment>()

                while (result.next()) {
                    set.add(getFromResultSet(result))
                }

                result.close()
                set
            }
        }
    }

    fun queryByTargetIp(ip: InetAddress): Set<Punishment> {
        return databaseManager.getConnection().use {
            return@use it.prepareStatement(nameReplacer.apply(QUERY_BY_TARGET_IP_SCHEMA)).use { s ->
                s.setString(1, ip.hostAddress)

                val result = s.executeQuery()
                val set = mutableSetOf<Punishment>()

                while (result.next()) {
                    set.add(getFromResultSet(result))
                }

                result.close()
                set
            }
        }
    }

    fun queryAll(): Set<Punishment> {
        return databaseManager.getConnection().use {
            return@use it.createStatement().use { s ->
                val result = s.executeQuery(nameReplacer.apply(QUERY_EVERYTHING_SCHEMA))
                val set = mutableSetOf<Punishment>()

                while (result.next()) {
                    set.add(getFromResultSet(result))
                }

                result.close()
                set
            }
        }
    }

    fun queryByType(type: BanType): Set<Punishment> {
        return databaseManager.getConnection().use {
            return@use it.prepareStatement(nameReplacer.apply(QUERY_EVERYTHING_BY_TYPE_SCHEMA)).use { s ->
                s.setString(1, if (type == BanTypes.IP) "ip" else "uuid")

                val result = s.executeQuery()
                val set = mutableSetOf<Punishment>()

                while (result.next()) {
                    set.add(getFromResultSet(result))
                }

                result.close()
                set
            }
        }
    }

}
