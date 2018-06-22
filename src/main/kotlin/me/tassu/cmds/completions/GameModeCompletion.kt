package me.tassu.cmds.completions

import me.tassu.cmds.ex.ArgumentCommandException
import me.tassu.util.text
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import kotlin.streams.toList

class GameModeCompletion(key: String) : CommandElement(key.text()) {

    override fun complete(src: CommandSource?, args: CommandArgs?, context: CommandContext?): MutableList<String> {
        return game.server.onlinePlayers.stream().filter { (src as? Player)?.canSee(it) ?: true }.map { it.name }.toList().toMutableList()
    }

    private val game: Game get() = Sponge.getGame()

    override fun parseValue(source: CommandSource?, args: CommandArgs?): GameMode? {
        val arg = args!!.next()

        return when (arg) {
            "c", "crea", "creative" -> GameModes.CREATIVE
            "s", "surv", "survival" -> GameModes.SURVIVAL
            "a", "adv", "adventure" -> GameModes.ADVENTURE
            "sp", "spec", "spectator" -> GameModes.SPECTATOR
            else -> {
                GameModes.NOT_SET
            }
        }
    }

}

