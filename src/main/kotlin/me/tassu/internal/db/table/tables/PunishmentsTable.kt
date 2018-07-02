package me.tassu.internal.db.table.tables

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.db.DatabaseManager
import me.tassu.internal.db.table.AbstractTable
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
                "`target_type` ENUM('uuid', 'id'), " +
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
        private const val QUERY_BY_TARGET_UUID_SCHEMA = "SELECT $SELECT_VARIABLES FROM %NAME WHERE target=?;"
        private const val QUERY_BY_TARGET_NAME_SCHEMA = "SELECT $SELECT_VARIABLES FROM %NAME WHERE target=(SELECT uuid FROM %USER_CACHE_NAME WHERE name=?);"
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

    override fun migrateFromOlderVersion(olderVersion: Int, connection: Connection): Int {
        if (olderVersion == getCurrentVersion()) {
            return getCurrentVersion()
        }

        connection.createStatement().use { s ->
            s.execute(nameReplacer.apply(CREATE_TABLE_SCHEMA))
        }

        return getCurrentVersion()
    }

    fun queryById(id: Int): ResultSet {
        return databaseManager.getConnection().use {
            return@use it.prepareStatement(nameReplacer.apply(QUERY_BY_ID_SCHEMA)).use { s ->
                s.setInt(1, id)
                s.executeQuery()
            }
        }
    }

    fun queryByTargetUuid(uuid: UUID): ResultSet {
        return databaseManager.getConnection().use {
            return@use it.prepareStatement(nameReplacer.apply(QUERY_BY_TARGET_UUID_SCHEMA)).use { s ->
                s.setString(1, uuid.toString())
                s.executeQuery()
            }
        }
    }

    fun queryByTargetName(name: String): ResultSet {
        return databaseManager.getConnection().use {
            return@use it.prepareStatement(nameReplacer.apply(QUERY_BY_TARGET_NAME_SCHEMA)).use { s ->
                s.setString(1, name)
                s.executeQuery()
            }
        }
    }

}
