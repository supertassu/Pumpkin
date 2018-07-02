package me.tassu.internal.cfg

import com.google.inject.Inject
import com.google.inject.Singleton
import com.google.inject.name.Named
import ninja.leaping.configurate.SimpleConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.Setting
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
            val out = SimpleConfigurationNode.root()
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

    @Setting
    var debug = true

    @Setting("enabled commands")
    var enabledCommands = listOf("gamemode")

    @Setting("enabled features")
    var enabledFeatures = listOf("chat")

}