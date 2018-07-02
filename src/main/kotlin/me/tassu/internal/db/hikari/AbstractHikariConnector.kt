package me.tassu.internal.db.hikari

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import me.tassu.internal.cfg.MainConfig
import me.tassu.internal.db.IDatabaseConnector
import java.sql.Connection
import java.sql.SQLException
import java.util.LinkedHashMap

abstract class AbstractHikariConnector : IDatabaseConnector {

    private lateinit var hikari: HikariDataSource

    override fun getConnection(): Connection {
        return hikari.connection
    }

    abstract val driverClass: String

    open fun appendConfigurationProperties(config: HikariConfig): HikariConfig { return config }

    override fun connect(databaseConfig: MainConfig.DatabaseConfig) {
        var config = HikariConfig()

        val address = databaseConfig.host
        val port = databaseConfig.port ?: 3306

        config.dataSourceClassName = driverClass
        config.maximumPoolSize = 20
        config.poolName = "Pumpkin Hikari Connector"
        config.username = databaseConfig.username
        config.password = databaseConfig.password
        config.addDataSourceProperty("serverName", address)
        config.addDataSourceProperty("port", port)
        config.addDataSourceProperty("databaseName", databaseConfig.database)

        config = appendConfigurationProperties(config)

        hikari = HikariDataSource(config)
    }

    override fun testConnection(): Map<String, String> {
        val ret = LinkedHashMap<String, String>()
        var success = true

        val start = System.currentTimeMillis()
        try {
            // ping server
            getConnection().use { c -> c.createStatement().use { s -> s.execute("SELECT 1") } }
        } catch (e: SQLException) {
            success = false
        }

        val duration = System.currentTimeMillis() - start

        if (success) {
            ret["Ping"] = "${duration}ms"
            ret["Connected"] = "true"
        } else {
            ret["Connected"] = "false"
        }

        return ret
    }

    override fun disconnect() {
        if (!hikari.isClosed) {
            hikari.close()
        }
    }

}