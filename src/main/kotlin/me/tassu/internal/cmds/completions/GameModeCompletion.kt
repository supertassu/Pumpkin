package me.tassu.internal.cmds.completions

import me.tassu.internal.util.kt.text
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.player.gamemode.GameModes

class GameModeCompletion(key: String) : CommandElement(key.text()) {

    override fun complete(src: CommandSource?, args: CommandArgs?, context: CommandContext?): MutableList<String> {
        return mutableListOf("CREATIVE", "SURVIVAL", "ADVENTURE", "SPECTATOR")
    }

    override fun parseValue(source: CommandSource?, args: CommandArgs?): PossibleContainer<GameMode>? {
        val arg = args!!.next()

        return when (arg.toLowerCase()) {
            "c", "crea", "creative" -> GameModes.CREATIVE.wrap()
            "s", "surv", "survival" -> GameModes.SURVIVAL.wrap()
            "a", "adv", "adventure" -> GameModes.ADVENTURE.wrap()
            "sp", "spec", "spectator" -> GameModes.SPECTATOR.wrap()
            else -> {
                PossibleContainer(null, arg)
            }
        }
    }

}
