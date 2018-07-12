package me.tassu.features.misc

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.internal.db.table.tables.UserCacheTable
import me.tassu.internal.feature.Feature
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.network.ClientConnectionEvent
import java.util.*
import java.util.concurrent.TimeUnit
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import me.tassu.internal.feature.SimpleFeature
import org.spongepowered.api.Game
import java.net.InetAddress

@Singleton
class UserDataFeature : SimpleFeature() {

    override val id: String = "user_data"

    override val listeners: List<Any> = listOf()
    override val permissions: List<String> = listOf()
    override val dependencies: List<String> = listOf()

    @Inject lateinit var table: UserCacheTable
    @Inject lateinit var game: Game

    @Listener
    fun onJoin(event: ClientConnectionEvent.Login) {
        val uuid = event.profile.uniqueId
        val name = event.profile.name.orElse("No known name.")
        val ip = event.connection.address.address

        val data = UserCacheTable.UserData(name, ip, uuid)
        table.update(data)
    }

    @Listener
    fun onQuit(event: ClientConnectionEvent.Disconnect) {
        nameCache.invalidate(event.targetEntity.name)
        ipCache.invalidate(event.targetEntity.connection.virtualHost.address)
        uuidCache.invalidate(event.targetEntity.uniqueId)
    }

    private val uuidCache: LoadingCache<UUID, UserCacheTable.UserData> by lazy {
        CacheBuilder.newBuilder()
                .maximumSize(game.server.maxPlayers.toLong())
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(object : CacheLoader<UUID, UserCacheTable.UserData>() {
                    override fun load(uuid: UUID?): UserCacheTable.UserData? {
                        if (uuid == null) return null
                        return table.queryUserByUUID(uuid)
                    }
                })
    }

    private val nameCache: LoadingCache<String, UUID> by lazy {
        CacheBuilder.newBuilder()
                .maximumSize(game.server.maxPlayers.toLong())
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(object : CacheLoader<String, UUID>() {
                    override fun load(name: String): UUID? {
                        val load = table.queryUserByName(name) ?: return null
                        uuidCache.put(load.uuid, load)
                        return load.uuid
                    }
                })
    }

    private val ipCache: LoadingCache<InetAddress, UUID> by lazy {
        CacheBuilder.newBuilder()
                .maximumSize(game.server.maxPlayers.toLong())
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .build(object : CacheLoader<InetAddress, UUID>() {
                    override fun load(ip: InetAddress): UUID? {
                        val load = table.queryUserByIp(ip) ?: return null
                        uuidCache.put(load.uuid, load)
                        return load.uuid
                    }
                })
    }

    override fun clearCache() {
        uuidCache.cleanUp()
    }

    fun queryUserByUUID(uuid: UUID): UserCacheTable.UserData? {
        return uuidCache.get(uuid)
    }

    fun queryUserByName(name: String) : UserCacheTable.UserData? {
        return uuidCache.get(nameCache.get(name))
    }

    fun queryUserByIp(ip: InetAddress) : UserCacheTable.UserData? {
        return uuidCache.get(ipCache.get(ip))
    }

}
