package me.tassu.internal.db.table

import java.sql.Connection

abstract class AbstractTable {

    abstract fun getName(): String
    abstract fun getCurrentVersion(): Int

    abstract fun migrateFromOlderVersion(olderVersion: Int, connection: Connection): Int

}