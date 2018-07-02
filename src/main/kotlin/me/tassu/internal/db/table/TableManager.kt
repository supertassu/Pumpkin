package me.tassu.internal.db.table

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.db.DatabaseManager
import me.tassu.internal.db.table.tables.UserCacheTable
import me.tassu.internal.db.table.tables.MetaTable
import me.tassu.internal.db.table.tables.PunishmentsTable

@Singleton
class TableManager {

    @Inject private lateinit var databaseManager: DatabaseManager

    @Inject private lateinit var metaTable: MetaTable
    @Inject private lateinit var punishmentsTable: PunishmentsTable
    @Inject private lateinit var userCacheTable: UserCacheTable

    fun init() {
        databaseManager.getConnection().use { it ->
            val tables = metaTable.load(it).toMutableMap()

            if (tables[userCacheTable.getName()] != userCacheTable.getCurrentVersion()) {
                tables[userCacheTable.getName()] = userCacheTable.migrateFromOlderVersion(tables[userCacheTable.getName()] ?: -1, it)
            }

            if (tables[punishmentsTable.getName()] != punishmentsTable.getCurrentVersion()) {
                tables[punishmentsTable.getName()] = punishmentsTable.migrateFromOlderVersion(tables[punishmentsTable.getName()] ?: -1, it)
            }

            metaTable.save(tables, it)

        }
    }


}