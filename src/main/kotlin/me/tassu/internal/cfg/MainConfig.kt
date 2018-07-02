package me.tassu.internal.cfg

import com.google.inject.Inject
import com.google.inject.Singleton
import com.google.inject.name.Named
import me.tassu.internal.db.DatabaseType
import ninja.leaping.configurate.SimpleConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.io.IOException

@Singleton
class MainConfig {

    private var configMapper: ObjectMapper<MainConfig>.BoundInstance = ObjectMapper.forObject(this)
    @Inject @Named("pumpkin") lateinit var loader: ConfigurationLoader<CommentedConfigurationNode>

    fun init() {
        this.reload()
        this.save()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun save() {
        try {
            val out = SimpleCommentedConfigurationNode.root()
            this.configMapper.serialize(out)
            this.loader.save(out)
        } catch (e: ObjectMappingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    fun reload() {
        try {
            this.configMapper.populate(this.loader.load())
        } catch (e: ObjectMappingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    @Setting(comment = "Enables / Disables Debug mode.")
    var debug = true

    @Setting("enabled commands", comment = "Sponge's nature allows enabling / disabling commands on the fly.\nThis array contains all enabled commands.")
    var enabledCommands = listOf("gamemode")

    @Setting("enabled features", comment = "This array contains all enabled features.")
    var enabledFeatures = listOf("chat")

    @Setting(comment = "Contains database configuration.")
    var database = DatabaseConfig()

    @ConfigSerializable
    class DatabaseConfig {

        @Setting
        var type = DatabaseType.MARIADB

        @Setting
        var host = "localhost"

        @Setting
        var port: Int? = 3306

        @Setting
        var username = "bart"

        @Setting
        var password = "please_create_a_better_password_than_this"

        @Setting
        var database = "minecraft"

    }

}