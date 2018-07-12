package me.tassu.cmds

import me.tassu.internal.cmds.completions.GameModeCompletion
import me.tassu.internal.cmds.completions.PlayerCompletion
import me.tassu.internal.cmds.completions.PossibleContainer
import me.tassu.internal.cmds.ex.InvalidUsageException
import me.tassu.internal.cmds.meta.AbstractCommand
import me.tassu.internal.util.kt.formatColoredMessage
import me.tassu.internal.util.kt.getAllMessageReceiversWithPermission
import me.tassu.internal.util.kt.sendColoredMessage
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode

class GamemodeCommand : AbstractCommand("Game mode", "gamemode", "gm") {

    override fun executeCommand(src: CommandSource, args: CommandContext): CommandResult {
        val rawPlayer = args.getOne<PossibleContainer<Player>>("player")
        val rawMode = args.getOne<PossibleContainer<GameMode>>("mode")

        if (!rawMode.isPresent) {
            throw InvalidUsageException("/gamemode <mode> [player]")
        }

        val mode = rawMode.get()

        if (!mode.isPresent()) {
            throw InvalidUsageException("/gamemode <mode> [player]")
        }

        if (!rawPlayer.isPresent && src !is Player) {
            throw InvalidUsageException("/gamemode <mode> [player]")
        }

        val player = if (rawPlayer.isPresent) rawPlayer.get() else PossibleContainer(src as Player)

        if (!player.isPresent()) {
            throw InvalidUsageException("/gamemode <mode> [player]")
        }

        player.get()!!.offer(Keys.GAME_MODE, mode.get()!!)

        if (src is Player && src.uniqueId == player.get()!!.uniqueId) {
            src.sendColoredMessage(generalMessages.commands.gamemode.setOwn,
                    "mode" to mode.get()!!.name)

            val message = generalMessages.commands.gamemode.otherSetOwn.formatColoredMessage(
                    "actor" to src.name,
                    "mode" to mode.get()!!.name,
                    "target" to player.get()!!.name)

            server.getAllMessageReceiversWithPermission("pumpkin.command.gamemode.view")
                    .filter { it != src }
                    .forEach { it.sendMessage(message) }

        } else {
            src.sendColoredMessage(generalMessages.commands.gamemode.setOther,
                    "mode" to mode.get()!!.name, "target" to player.get()!!.name)

            val message = generalMessages.commands.gamemode.otherSetOther.formatColoredMessage(
                    "actor" to src.name,
                    "mode" to mode.get()!!.name,
                    "target" to player.get()!!.name)

            server.getAllMessageReceiversWithPermission("pumpkin.command.gamemode.view")
                    .filter { it != src }
                    .forEach { it.sendMessage(message) }
        }

        return CommandResult.affectedEntities(1)
    }

    override val arguments: Array<CommandElement> = arrayOf(
            GenericArguments.onlyOne(GenericArguments.optional(GameModeCompletion("mode"))),
            GenericArguments.onlyOne(GenericArguments.optional(PlayerCompletion("player", true)))
    )

    override val permissions: List<String> = listOf(*super.permissions.toTypedArray(), "view")

}