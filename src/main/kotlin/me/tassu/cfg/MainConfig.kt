package me.tassu.cfg

import ninja.leaping.configurate.SimpleConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.Setting
import java.io.IOException

class MainConfig(private val loader: ConfigurationLoader<CommentedConfigurationNode>) {

    @Setting("debug", comment = "Enables / Disables Debug mode. ")
    var debug = true

    @Setting("enabled commands", comment = "Sponge's nature allows enabling / disabling commands on the fly.\nThis array contains all enabled commands.")
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
            this.loader.defaultOptions.header = """
                  ____  __ __ ___  ___ ____  __ __ __ __  __
                  || \\ || || ||\\//|| || \\ || // || ||\ ||
                  ||_// || || || \/ || ||_// ||<<  || ||\\||
                  ||    \\_// ||    || ||    || \\ || || \||

                    This is the primary configuration file
                    of Pumpkin, the powerful server core plugin.

                    It handles everything from teleporting admins
                    to punishing people that broke the rules.

                    TASSU ==========
                    https://tassu.me
                    pumpkin@tassu.me


                    This configuration file uses the Human-Optimized Config Object Notation (HOCON) for formatting.
                    Learn more at https://github.com/lightbend/config/blob/master/HOCON.md
            """.trimIndent()
            this.loader.save(out)
        } catch (e: ObjectMappingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

    private fun load() {
        try {
            val loaded = this.loader.load()
            this.configMapper!!.populate(loaded)
        } catch (e: ObjectMappingException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }


}