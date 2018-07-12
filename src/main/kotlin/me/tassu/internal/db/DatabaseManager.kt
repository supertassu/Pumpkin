package me.tassu.internal.db

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.cfg.MainConfig
import me.tassu.internal.db.table.TableManager
import me.tassu.internal.di.PumpkinHolder
import me.tassu.internal.util.PumpkinLog
import java.sql.Connection

@Singleton
class DatabaseManager {

    @Inject private lateinit var mainConfig: MainConfig
    @Inject private lateinit var log: PumpkinLog

    @Inject private lateinit var tableManager: TableManager

    lateinit var tablePrefix: String

    private var connected = false
    private val type: DatabaseType get() = mainConfig.database.type
    private lateinit var connector: IDatabaseConnector

    /**
     * Connects to the database.
     */
    fun connect() {
        if (connected) {
            throw IllegalStateException("Already connected.")
        }

        tablePrefix = mainConfig.database.tablePrefix

        connector = type.clazz.newInstance()
        PumpkinHolder.getInstance().injector.injectMembers(connector)
        connector.connect(mainConfig.database)

        tableManager.init()

        connected = true
    }

    fun disconnect() {
        if (!connected) {
            throw IllegalStateException("Not connected yet.")
        }

        connector.disconnect()
        connected = false
    }

    /**
     * Pings the database.
     */
    fun ping(): Map<String, String> {
        if (!connected) {
            throw IllegalStateException("Not connected yet. Call #connect() to connect.")
        }

        val ping = connector.testConnection()

        if (!ping.containsKey("Connected")) {
            log.warn("Ping did not return key Connected, assuming there is an error with the connection.")
        }

        connected = ping["Connected"]?.toBoolean() ?: false

        return ping
    }

    fun getConnection(): Connection {
        if (!::connector.isInitialized) {
            throw IllegalStateException("Not connected yet. Call #connect() to connect.")
        }

        return connector.getConnection()
    }

    fun isConnected(): Boolean {
        return connected
    }

}