package me.tassu.internal.db.hikari

class MariaDbConnector : AbstractHikariConnector() {

    override val driverClass: String = "org.mariadb.jdbc.MariaDbDataSource"

}