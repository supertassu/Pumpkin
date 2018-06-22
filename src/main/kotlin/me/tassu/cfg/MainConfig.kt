package me.tassu.cfg

import ninja.leaping.configurate.SimpleConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.Setting
import java.io.IOException


class MainConfig(private val loader: ConfigurationLoader<CommentedConfigurationNode>) {

    @Setting("pumpkin.core.debug", comment = "Enables / Disables Debug mode. ")
    var debug = true

    @Setting("pumpkin.core.enabled commands")
    var enabledCommands: List<String> = listOf()

    private var configMapper: ObjectMapper<MainConfig>.BoundInstance? = null

    init {
        try {
            this.configMapper = ObjectMapper.forObject(this)
        } catch (e: ObjectMappingException) {
            e.printStackTrace()
        }

        this.load()
    }

    fun save() {
        try {
            val out = SimpleConfigurationNode.root()
            this.configMapper!!.serialize(out)
            this.loader.save(out)
        } catch (e: ObjectMappingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun load() {
        try {
            this.configMapper!!.populate(this.loader.load())
        } catch (e: ObjectMappingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


}