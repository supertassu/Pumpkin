package me.tassu.cmds.args

import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.text.Text

object Arguments {

    class GameModeArgument(key: Text) : CommandElement(key) {
        override fun parseValue(source: CommandSource?, args: CommandArgs?): Any? {
            return when (args!!.next().toLowerCase()) {
                "s", "surv", "survival" -> GameModes.SURVIVAL
                "c", "crea", "creative" -> GameModes.CREATIVE
                "a", "adv", "adventure" -> GameModes.ADVENTURE
                "sp", "spec", "spectator" -> GameModes.SPECTATOR
                else -> {
                    return GameModes.NOT_SET
                }
            }
        }

        override fun complete(src: CommandSource?, args: CommandArgs?, context: CommandContext?): MutableList<String> {
            return mutableListOf("SURVIVAL", "CREATIVE", "ADVENTURE", "SPECTATOR")
        }
    }

}