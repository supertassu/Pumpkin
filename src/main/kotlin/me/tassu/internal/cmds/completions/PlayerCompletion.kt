package me.tassu.internal.cmds.completions

import me.tassu.internal.util.kt.text
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.entity.living.player.Player
import kotlin.streams.toList

class PlayerCompletion(key: String, private var useSender: Boolean) : CommandElement(key.text()) {

    override fun complete(src: CommandSource?, args: CommandArgs?, context: CommandContext?): MutableList<String> {
        return game.server.onlinePlayers.stream().filter { (src as? Player)?.canSee(it) ?: true }.map { it.name }.toList().toMutableList()
    }

    private val game: Game get() = Sponge.getGame()

    override fun parseValue(src: CommandSource?, args: CommandArgs?): PossibleContainer<Player>? {
        val arg = args!!.next()

        if (arg.trim().isEmpty()) {
            return if (useSender && src is Player) {
                PossibleContainer(src)
            } else {
                PossibleContainer(null, "<none>")
            }
        }

        val first = game.server.onlinePlayers.stream().filter { it.name.toLowerCase() == arg.toLowerCase() }.findFirst()

        return if (first.isPresent) {
            PossibleContainer(first.get())
        } else {
            PossibleContainer(null, arg)
        }
    }

}

