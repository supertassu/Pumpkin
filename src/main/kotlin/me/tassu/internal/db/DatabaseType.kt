package me.tassu.internal.db

import me.tassu.internal.db.hikari.MariaDbConnector

/**
 * All database types.
 */
enum class DatabaseType(val clazz: Class<out IDatabaseConnector>) {

    MARIADB(MariaDbConnector::class.java);

}