package me.tassu.internal.db.table.tables

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.db.DatabaseManager
import me.tassu.internal.db.table.AbstractTable
import java.net.InetAddress
import java.sql.Connection
import java.util.*
import java.util.function.Function

@Singleton
class UserCacheTable : AbstractTable() {

    data class UserData(val name: String, val ip: InetAddress, val uuid: UUID)

    companion object {
        const val CREATE_TABLE_SCHEMA = "CREATE TABLE %NAME (`uuid` VARCHAR(36) PRIMARY KEY NOT NULL, `ip` VARCHAR(15) NOT NULL, `name` VARCHAR(16) NOT NULL);"
        const val UPDATE_SCHEMA = "INSERT INTO %NAME (uuid, ip, name) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE ip=?, name=?;"
        const val QUERY_BY_NAME = "SELECT uuid, name, ip FROM %NAME WHERE name=?;"
        const val QUERY_BY_UUID = "SELECT uuid, name, ip FROM %NAME WHERE uuid=?;"
        const val QUERY_BY_IP = "SELECT uuid, name, ip FROM %NAME WHERE ip=?;"
    }

    private val nameReplacer = Function<String, String> {
        it.replace("%NAME", getName())
    }

    @Inject
    private lateinit var databaseManager: DatabaseManager

    override fun getName(): String {
        return databaseManager.tablePrefix + "users"
    }

    override fun getCurrentVersion(): Int {
        return 1
    }

    override fun migrateFromOlderVersion(olderVersion: Int, connection: Connection): Int {
        if (olderVersion == getCurrentVersion()) {
            return getCurrentVersion()
        }

        connection.createStatement().use { s ->
            s.execute(nameReplacer.apply(CREATE_TABLE_SCHEMA))
        }

        return getCurrentVersion()
    }

    fun queryUserByUUID(uuid: UUID): UserData {
        return databaseManager.getConnection().use {
            it.prepareStatement(nameReplacer.apply(QUERY_BY_UUID)).use { s ->
                s.setString(1, uuid.toString())
                s.executeQuery().use {
                    UserData(it.getString("name"), InetAddress.getByName(it.getString("ip")), UUID.fromString(it.getString("uuid")))
                }
            }
        }
    }

    fun queryUserByIp(ip: InetAddress): UserData {
        return databaseManager.getConnection().use {
            it.prepareStatement(nameReplacer.apply(QUERY_BY_IP)).use { s ->
                s.setString(1, ip.hostName)
                s.executeQuery().use {
                    UserData(it.getString("name"), InetAddress.getByName(it.getString("ip")), UUID.fromString(it.getString("uuid")))
                }
            }
        }
    }

    fun queryUserByName(name: String): UserData {
        return databaseManager.getConnection().use {
            it.prepareStatement(nameReplacer.apply(QUERY_BY_NAME)).use { s ->
                s.setString(1, name)
                s.executeQuery().use {
                    UserData(it.getString("name"), InetAddress.getByName(it.getString("ip")), UUID.fromString(it.getString("uuid")))
                }
            }
        }
    }

    fun update(data: UserData) {
        databaseManager.getConnection().use {
            it.prepareStatement(nameReplacer.apply(UPDATE_SCHEMA)).use { s ->
                s.setString(1, data.uuid.toString())
                s.setString(2, data.ip.hostAddress)
                s.setString(3, data.name)
                s.setString(4, data.ip.hostAddress)
                s.setString(5, data.name)
            }
        }
    }

}