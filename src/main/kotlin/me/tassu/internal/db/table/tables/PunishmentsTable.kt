package me.tassu.internal.db.table.tables

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.db.DatabaseManager
import me.tassu.internal.db.table.AbstractTable
import java.sql.Connection
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
                "`reason` TEXT NOT NULL, " +
                "`revoked_on` TIMESTAMP, " +
                "`revoked_by` VARCHAR(36), " +
                "`revoke_reason` TEXT, " +
                "`flags` TEXT NOT NULL);"
    }

    private val nameReplacer = Function<String, String> {
        it.replace("%NAME", getName())
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
}
