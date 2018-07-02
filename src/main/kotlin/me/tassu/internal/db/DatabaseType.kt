package me.tassu.internal.db

import me.tassu.internal.db.hikari.MariaDbConnector

enum class DatabaseType(val clazz: Class<out IDatabaseConnector>) {

    MARIADB(MariaDbConnector::class.java);

}