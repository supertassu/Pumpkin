package me.tassu.msg

import ninja.leaping.configurate.SimpleConfigurationNode
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.Setting
import org.spongepowered.api.text.Text
import org.spongepowered.api.text.TextTemplate
import org.spongepowered.api.text.TextTemplate.arg
import org.spongepowered.api.text.TextTemplate.of
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import java.io.IOException


class GeneralMessages(private val loader: ConfigurationLoader<CommentedConfigurationNode>) {

    @Setting("prefix", comment = "Prefix applied to all messages.")
    var prefix: Text = Text.builder("(Pumpkin) ").color(TextColors.BLUE).build()

    @Setting("commands.no permissions")
    var msgNoPermissions: TextTemplate = of(
            arg("textColor"), "You do not have the required permission (",
            arg("highlightColor"), arg("missingPermission"), arg("textColor"), ")."
    )

    @Setting("commands.invalid usage")
    var msgInvalidUsage: TextTemplate = of(
            TextColors.GRAY, "This command is used like so: ", arg("usage").color(TextColors.BLUE)
    )

    @Setting("commands.invalid arguments")
    var msgInvalidArguments: TextTemplate = of(
            TextColors.GRAY, "The value ", arg("given").color(TextColors.BLUE),
            TextColors.GRAY, " can not be converted to a ", arg("expected").color(TextColors.BLUE),
            TextColors.GRAY, "."
    )

    @Setting("commands.error")
    var msgCommandException: TextTemplate = of(
            TextColors.RED, "The following error happened whilst executing this command: ", arg("error").color(TextColors.DARK_RED)
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
