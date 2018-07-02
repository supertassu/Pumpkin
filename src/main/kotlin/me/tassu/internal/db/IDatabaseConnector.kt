package me.tassu.internal.db

import me.tassu.internal.cfg.MainConfig
import java.sql.Connection

interface IDatabaseConnector {

    fun connect(databaseConfig: MainConfig.DatabaseConfig)
    fun getConnection(): Connection
    fun testConnection(): Map<String, String>
    fun disconnect()

}