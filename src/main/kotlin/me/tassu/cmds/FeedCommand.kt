package me.tassu.cmds

import com.google.inject.Singleton
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

@Singleton
class FeedCommand : AbstractCommand("Feed", "feed", "eat") {

    override fun executeCommand(src: CommandSource, args: CommandContext): CommandResult {
        val rawPlayer = args.getOne<PossibleContainer<Player>>("player")

        if (!rawPlayer.isPresent && src !is Player) {
            throw InvalidUsageException("/feed [player]")
        }

        val player = if (rawPlayer.isPresent) rawPlayer.get() else PossibleContainer(src as Player)

        if (!player.isPresent()) {
            throw InvalidUsageException("/feed [status]")
        }

        player.get()!!.offer(Keys.FOOD_LEVEL, 20)
        player.get()!!.offer(Keys.SATURATION, 20.toDouble())

        if (src is Player && src.uniqueId == player.get()!!.uniqueId) {
            src.sendColoredMessage(generalMessages.commands.feed.fedSelf)

            val message = generalMessages.commands.feed.otherFedSelf.formatColoredMessage(
                    "actor" to src.name)

            server.getAllMessageReceiversWithPermission("pumpkin.command.feed.view")
                    .filter { it != src }
                    .forEach { it.sendMessage(message) }

        } else {
            src.sendColoredMessage(generalMessages.commands.feed.fedOther,
                    "target" to player.get()!!.name)

            val message = generalMessages.commands.feed.otherFedOther.formatColoredMessage(
                    "actor" to src.name,
                    "target" to player.get()!!.name)

            server.getAllMessageReceiversWithPermission("pumpkin.command.feed.view")
                    .filter { it != src }
                    .forEach { it.sendMessage(message) }
        }

        return CommandResult.affectedEntities(1)
    }

    override val arguments: Array<CommandElement> = arrayOf(
            GenericArguments.onlyOne(GenericArguments.optional(PlayerCompletion("player", true)))
    )

    override val permissions: List<String> = listOf(*super.permissions.toTypedArray(), "view")

}