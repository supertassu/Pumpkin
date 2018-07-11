package me.tassu.internal.db

import me.tassu.internal.db.hikari.MySqlConnector

/**
 * All database types.
 */
enum class DatabaseType(val clazz: Class<out IDatabaseConnector>) {

    MYSQL(MySqlConnector::class.java);

}