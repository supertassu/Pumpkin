package me.tassu.cmds

import me.tassu.internal.cmds.completions.PlayerCompletion
import me.tassu.internal.cmds.completions.PossibleContainer
import me.tassu.internal.cmds.ex.InvalidUsageException
import me.tassu.internal.cmds.meta.PumpkinCommand
import me.tassu.internal.util.kt.*
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.Text

class TeleportCommand : PumpkinCommand("Teleport", "teleport", "tp") {

    override val arguments: Array<CommandElement> = arrayOf(
            GenericArguments.onlyOne(GenericArguments.optional(PlayerCompletion("player", true))),
            GenericArguments.onlyOne(GenericArguments.optional(PlayerCompletion("target", false)))
    )

    override fun executeCommand(src: CommandSource, args: CommandContext): CommandResult {
        var rawPlayer = args.getOne<PossibleContainer<Player>>("player")
        var rawTarget = args.getOne<PossibleContainer<Player>>("target")

        if (rawPlayer.isPresent && !rawTarget.isPresent && src is Player) {
            rawTarget = rawPlayer
            rawPlayer = PossibleContainer(src).toOptional()
        } else if (!rawPlayer.isPresent || !rawTarget.isPresent) {
            throw InvalidUsageException("/teleport [target] <to>")
        }

        if (!rawPlayer.get().isPresent() || !rawTarget.get().isPresent()) {
            throw InvalidUsageException("/teleport [target] <to>")
        }

        val player: Player = rawPlayer.get().get()!!
        val target: Player = rawTarget.get().get()!!

        player.location = target.location

        if (src is Player && src.uniqueId == player.uniqueId) {
            src.sendColoredMessage(generalMessages.commands.teleport.teleportSelf,
                    "target" to target.name)

            val message = generalMessages.commands.teleport.otherTeleportSelf.formatColoredMessage(
                    "actor" to src.name,
                    "target" to target.name)

            server.getAllMessageReceiversWithPermission("pumpkin.command.teleport.view")
                    .filter { it != src }
                    .forEach { it.sendMessage(message) }

        } else {
            src.sendColoredMessage(generalMessages.commands.teleport.teleportOther,
                    "player" to player.name, "target" to target.name)

            val message = generalMessages.commands.teleport.otherTeleportOther.formatColoredMessage(
                    "actor" to src.name,
                    "player" to player.name,
                    "target" to target.name)

            server.getAllMessageReceiversWithPermission("pumpkin.command.teleport.view")
                    .filter { it != src }
                    .forEach { it.sendMessage(message) }
        }

        return CommandResult.affectedEntities(1)
    }

    override val permissions: List<String> = listOf(*super.permissions.toTypedArray(), "view")

}