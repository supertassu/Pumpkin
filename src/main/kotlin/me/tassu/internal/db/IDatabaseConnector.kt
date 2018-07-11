package me.tassu.internal.db

import me.tassu.internal.cfg.MainConfig
import java.sql.Connection

/**
 * Represents a thing that can connect to a sql database.
 */
interface IDatabaseConnector {

    fun connect(databaseConfig: MainConfig.DatabaseConfig)
    fun getConnection(): Connection
    fun testConnection(): Map<String, String>
    fun disconnect()

}