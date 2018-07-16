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
        var msgPrefix = "&a(Pumpkin) "

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

        @Setting(comment = "The prefix will not be included if not specified manually. No worries.")
        var prefixes = Prefix()

        @ConfigSerializable
        class Suffix {
            @Setting
            val present = " {{value}}"

            @Setting
            var none = ""
        }

        @Setting(comment = "The prefix will not be included if not specified manually. No worries.")
        val suffixes = Suffix()

        @Setting
        var format = "NOPREFIX|&a{{user_prefix}}{{user_name}}{{user_suffix}}&7: &f{{text}}"
    }

    @Setting
    var commands = CommandMessages()

    @ConfigSerializable
    class CommandMessages {
        @Setting
        var meta = Meta()

        @ConfigSerializable
        class Meta {
            @Setting("no permissions")
            var noPerms = "&7You do not have the required permission (&2{{perm}}&7) to execute this command."

            @Setting
            var usage = "&7This command is used like &2{{usage}}"

            @Setting
            var error = "NOPREFIX|&4(Pumpkin) &cThe following error happened whilst executing the command: &4{{error}}"
        }

        @Setting
        var gamemode = GameMode()

        @ConfigSerializable
        class GameMode {
            @Setting("msg self own")
            var setOwn = "&7Your game mode was set to &2{{mode}}&7."

            @Setting("msg self other")
            var setOther = "&7Game mode of &2{{target}}&7 was set to &2{{mode}}&7."

            @Setting("msg others own")
            var otherSetOwn = "&2{{actor}}&7 set own game mode to &2{{mode}}&7."

            @Setting("msg others other")
            var otherSetOther = "&2{{actor}}&7 set game mode of &2{{target}}&7 to &2{{mode}}&7."
        }

        @Setting
        var teleport = Teleport()

        @ConfigSerializable
        class Teleport {
            @Setting("msg self self")
            var teleportSelf = "&7Teleported to &2{{target}}&7."

            @Setting("msg self other")
            var teleportOther = "&7Teleported &2{{player}}&7 to &2{{target}}&7."

            @Setting("msg other self")
            var otherTeleportSelf = "&2{{actor}}&7 teleported themselves to &2{{target}}&7."

            @Setting("msg other other")
            var otherTeleportOther = "&2{{actor}}&7 teleported &2{{player}}&7 to &2{{target}}&7."
        }

        @Setting
        val fly = Fly()

        @ConfigSerializable
        class Fly {
            @Setting("msg self own")
            var setOwn = "&7Your flight mode was set to &2{{mode}}&7."

            @Setting("msg self other")
            var setOther = "&7Flight mode of &2{{target}}&7 was set to &2{{mode}}&7."

            @Setting("msg others own")
            var otherSetOwn = "&2{{actor}}&7 set own flight mode to &2{{mode}}&7."

            @Setting("msg others other")
            var otherSetOther = "&2{{actor}}&7 set flight mode of &2{{target}}&7 to &2{{mode}}&7."
        }

        @Setting
        var pumpkin = Pumpkin()

        @ConfigSerializable
        class Pumpkin {
            @Setting
            var fallback = listOf(
                    "NOPREFIX| &aPUMPKIN &aver. {{version}}",
                    "NOPREFIX| &7* &aEnabled modules: &7{{enabled modules}}",
                    "NOPREFIX| &7* &aDisabled modules: &7{{disabled modules}}"
            )

            @Setting
            var reloaded = "Successfully reloaded. See console for more details."
        }

    }

    @Setting
    val punishments = Punishments()

    @ConfigSerializable
    class Punishments {

        @Setting
        val commands = Commands()

        @ConfigSerializable
        class Commands {

            @Setting("pardon no such ban")
            val pardonBanNotFound = "&7No bans were found for &2{{target}}&7."

            @Setting("pardon msg to self")
            val pardonedSelf = "&2{{target}}&7 was pardoned due to &2{{reason}}&7."

            @Setting("pardon msg to others")
            val pardonedOther = "&2{{target}}&7 was pardoned by &2{{actor}}&7 due to &2{{reason}}&7."

            @Setting
            val alreadyBanned = "&7The user &2{{target}}&7 is already banned for &2{{reason}}&7."

            @Setting("permaban msg to self")
            val bannedSelf = "&2{{target}}&7 was permanently banned due to &2{{reason}}&7."

            @Setting("permaban msg to others")
            val bannedOther = "&2{{target}}&7 was permanently banned by &2{{actor}}&7 due to &2{{reason}}&7."

        }

        @Setting("msg to perma banned")
        val permaBannedMsg = "NOPREFIX| &cBANNED BY &4{{actor}}\n&7&o{{reason}}"

    }

}
