package me.tassu.internal.cfg

import com.google.inject.Inject
import com.google.inject.Singleton
import com.google.inject.name.Named
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.commented.SimpleCommentedConfigurationNode
import ninja.leaping.configurate.loader.ConfigurationLoader
import ninja.leaping.configurate.objectmapping.ObjectMapper
import ninja.leaping.configurate.objectmapping.ObjectMappingException
import ninja.leaping.configurate.objectmapping.Setting
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable
import java.io.IOException

@Singleton
class GeneralMessages {

    private var configMapper: ObjectMapper<GeneralMessages>.BoundInstance = ObjectMapper.forObject(this)
    @Inject @Named("messages") lateinit var loader: ConfigurationLoader<CommentedConfigurationNode>

    fun init() {
        this.reload()
        this.save()
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun save() {
        try {
            val out = SimpleCommentedConfigurationNode.root()
            out.setComment("""
                  ____  __ __ ___  ___ ____  __ __ __ __  __
                  || \\ || || ||\\//|| || \\ || // || ||\ ||
                  ||_// || || || \/ || ||_// ||<<  || ||\\||
                  ||    \\_// ||    || ||    || \\ || || \||

                    This is the translation file of Pumpkin,
                    the powerful server core plugin.

                    TASSU ==========
                    https://tassu.me
                    pumpkin@tassu.me


                    Prefix a string with "NOPREFIX|" to make the prefix not appear before the message.

                    This configuration file uses the Human-Optimized Config Object Notation (HOCON) for formatting.
                    Learn more at https://github.com/lightbend/config/blob/master/HOCON.md
            """.trimIndent())
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
    var meta = MetaMessages()

    @ConfigSerializable
    class MetaMessages {

        @Setting("prefix")
        var msgPrefix = "&9(Pumpkin) "

    }

    @Setting
    var chat = ChatMessages()

    @ConfigSerializable
    class ChatMessages {

        @ConfigSerializable
        class Prefix {
            @Setting
            var present = "{{value}} "

            @Setting
            var none = ""
        }

        @Setting
        var prefixes = Prefix()

        @ConfigSerializable
        class Suffix {
            @Setting
            val present = " {{value}}"

            @Setting
            var none = ""
        }

        @Setting
        val suffixes = Suffix()

        @Setting
        var format = "NOPREFIX|&a{{user_prefix}}{{user_name}}{{user_suffix}}&7: &f{{text}}"
    }

    @Setting
    var commands = CommandMessages()

    @ConfigSerializable
    class CommandMessages {
        @Setting("no permissions")
        var noPerms = "&7You do not have the required permission (&9{{perm}}&7) to execute this command."

        @Setting
        var usage = "&7This command is used like &9{{usage}}"

        @Setting
        var args = "&7The value &9{{given}}&7 can not be converted to a &9{{expected}}&7."

        @Setting
        var error = "NOPREFIX|&4(Pumpkin) &cThe following error happened whilst executing the command: &4{{error}}"

        @Setting
        val gamemode = GameMode()

        @ConfigSerializable
        class GameMode {
            @Setting("msg self own")
            var setOwn = "&7Your game mode was set to &9{{mode}}&7."

            @Setting("msg set other")
            var setOther = "&7Game mode of &9{{target}}&7 was set to &9{{mode}}&7."

            @Setting("msg others own")
            var otherSetOwn = "&7[&9{{actor}}&7]: Updated own game mode to &9{{mode}}&7."

            @Setting("msg others other")
            var otherSetOther = "&7[&9{{actor}}&7]: Updated game mode of &9{{target}}&7 to &9{{mode}}&7."
        }
    }

}
