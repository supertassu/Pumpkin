package me.tassu.internal.db.hikari

class MySqlConnector : AbstractHikariConnector() {

    override val driverClass: String = "com.mysql.cj.jdbc.MysqlDataSource"

}