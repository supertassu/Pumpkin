package me.tassu.cmds

import me.tassu.internal.cmds.completions.PlayerCompletion
import me.tassu.internal.cmds.completions.PossibleContainer
import me.tassu.internal.cmds.completions.TriStateCompletion
import me.tassu.internal.cmds.ex.InvalidUsageException
import me.tassu.internal.cmds.meta.AbstractCommand
import me.tassu.internal.util.kt.*
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.util.Tristate

class FlyCommand : AbstractCommand("Fly", "fly", "f", "flight") {

    override fun executeCommand(src: CommandSource, args: CommandContext): CommandResult {
        val rawPlayer = args.getOne<PossibleContainer<Player>>("player")
        var rawMode = args.getOne<PossibleContainer<Tristate>>("mode")

        if (!rawMode.isPresent) {
            rawMode = PossibleContainer(Tristate.UNDEFINED).toOptional()
        }

        if (!rawPlayer.isPresent && src !is Player) {
            throw InvalidUsageException("/fly [status] [player]")
        }

        val player = if (rawPlayer.isPresent) rawPlayer.get() else PossibleContainer(src as Player)

        if (!player.isPresent()) {
            throw InvalidUsageException("/fly [status] [player]")
        }

        val mode = when (rawMode.get().get()) {
            Tristate.TRUE -> true
            Tristate.FALSE -> false
            else -> {
                val value = player.get()!!.getValue(Keys.CAN_FLY)
                if (value.isPresent) !value.get().get() else true
            }
        }.toOptional()

        if (!mode.isPresent) {
            throw InvalidUsageException("/fly [status] [player]")
        }

        player.get()!!.offer(Keys.CAN_FLY, mode.get())
        player.get()!!.offer(Keys.IS_FLYING, mode.get())

        val new = mode.get()

        if (src is Player && src.uniqueId == player.get()!!.uniqueId) {
            src.sendColoredMessage(generalMessages.commands.fly.setOwn,
                    "mode" to new)

            val message = generalMessages.commands.fly.otherSetOwn.formatColoredMessage(
                    "actor" to src.name,
                    "mode" to new,
                    "target" to player.get()!!.name)

            server.getAllMessageReceiversWithPermission("pumpkin.command.fly.view")
                    .filter { it != src }
                    .forEach { it.sendMessage(message) }

        } else {
            src.sendColoredMessage(generalMessages.commands.fly.setOther,
                    "mode" to new, "target" to player.get()!!.name)

            val message = generalMessages.commands.fly.otherSetOther.formatColoredMessage(
                    "actor" to src.name,
                    "mode" to new,
                    "target" to player.get()!!.name)

            server.getAllMessageReceiversWithPermission("pumpkin.command.fly.view")
                    .filter { it != src }
                    .forEach { it.sendMessage(message) }
        }

        return CommandResult.affectedEntities(1)
    }

    override val arguments: Array<CommandElement> = arrayOf(
            GenericArguments.onlyOne(GenericArguments.optional(TriStateCompletion("mode"))),
            GenericArguments.onlyOne(GenericArguments.optional(PlayerCompletion("player", true)))
    )

    override val permissions: List<String> = listOf(*super.permissions.toTypedArray(), "view")

}