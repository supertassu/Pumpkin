package me.tassu.msg

import ninja.leaping.configurate.SimpleConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.Setting
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate.arg
import org.spongepowered.api.text.TextTemplate.of
import org.spongepowered.api.text.format.TextColors
import java.io.IOException


class GeneralMessages(private val loader: ConfigurationLoader<CommentedConfigurationNode>) {

    @Setting("pumpkin.messages.meta.prefix")
    var prefix = Text.builder("(Pumpkin) ").color(TextColors.BLUE).build()

    @Setting
    var textColor = TextColors.GRAY

    @Setting
    var highlightColor = TextColors.BLUE

    @Setting
    var msgNoPermissions = of(
            arg("prefix"), arg("textColor"), "You do not have the required permission (",
            arg("highlightColor"), arg("missingPermission"), arg("textColor"), ")."
    )

    private var configMapper: ObjectMapper<GeneralMessages>.BoundInstance? = null

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
