package me.tassu.internal.db.table.tables

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.db.DatabaseManager
import me.tassu.internal.db.table.AbstractTable
import me.tassu.internal.util.PumpkinLog
import java.sql.Connection
import java.util.function.Function

@Singleton
class MetaTable : AbstractTable() {

    companion object {
        private const val SHOW_TABLES_SCHEMA  = "SHOW TABLES LIKE '%NAME';"
        private const val CREATE_TABLE_SCHEMA = "CREATE TABLE %NAME (`name` VARCHAR(64) PRIMARY KEY NOT NULL, `schema_version` INTEGER NOT NULL);"
        private const val INSERT_ENTRY_SCHEMA = "INSERT INTO %NAME (name, schema_version) VALUES (?, ?) ON DUPLICATE KEY UPDATE schema_version=?;"
        private const val SELECT_ENTRY_SCHEMA = "SELECT name, schema_version FROM %NAME;"
    }

    private val nameReplacer = Function<String, String> {
        it.replace("%NAME", getName())
    }

    @Inject private lateinit var databaseManager: DatabaseManager
    @Inject private lateinit var log: PumpkinLog


    override fun getName(): String {
        return databaseManager.tablePrefix + "meta"
    }

    override fun getCurrentVersion(): Int {
        return 1
    }

    override fun migrateFromOlderVersion(olderVersion: Int, connection: Connection): Int {
        return getCurrentVersion()
    }

    lateinit var loaded: Map<String, Int>

    fun load(it: Connection): Map<String, Int> {
        return it.createStatement().use { s ->
            val result = s.executeQuery(nameReplacer.apply(SHOW_TABLES_SCHEMA))
            val map = mutableMapOf(getName() to getCurrentVersion())

            if (!result.next()) {
                s.execute(nameReplacer.apply(CREATE_TABLE_SCHEMA))

                it.prepareStatement(nameReplacer.apply(INSERT_ENTRY_SCHEMA)).use { s ->
                    s.setString(1, getName())
                    s.setInt(2, getCurrentVersion())
                    s.setInt(3, getCurrentVersion())
                    s.execute()
                }

                log.debug("Table ${getName()} was created.", "MetaTable#load()")
            }

            s.executeQuery(nameReplacer.apply(SELECT_ENTRY_SCHEMA)).use { tables ->
                while (tables.next()) {
                    val name = tables.getString("name")
                    val version = tables.getInt("schema_version")
                    map[name] = version
                }
            }

            log.debug("Found schema versions for ${map.size} tables.", "MetaTable#load()")

            result.close()
            loaded = map

            map[getName()] = getCurrentVersion()
            map
        }
    }

    fun save(map: Map<String, Int>, it: Connection) {
        map.forEach { table, version ->
            if (!(loaded.containsKey(table) && loaded[table] == version)) {
                log.debug("Updated schema version of table $table to $version.", "MetaTable#load()")
                it.prepareStatement(nameReplacer.apply(INSERT_ENTRY_SCHEMA)).use { s ->
                    s.setString(1, table)
                    s.setInt(2, version)
                    s.setInt(3, version)
                    s.execute()
                }
            }
        }
    }

}