package me.tassu.internal.cmds.meta

import me.tassu.internal.cmds.ex.ArgumentCommandException
import me.tassu.internal.cmds.ex.InternalCommandException
import me.tassu.internal.cmds.ex.InvalidUsageException
import me.tassu.internal.cmds.ex.PermissionCommandException
import me.tassu.internal.cfg.GeneralMessages.cmdArgs
import me.tassu.internal.cfg.GeneralMessages.cmdError
import me.tassu.internal.cfg.GeneralMessages.cmdUsage
import me.tassu.internal.cfg.GeneralMessages.cmdNoPerms
import me.tassu.internal.util.sendColoredMessage
import org.spongepowered.api.Game
import org.spongepowered.api.Server
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.plugin.PluginContainer

abstract class PumpkinCommand(private val name: String) : CommandExecutor {

    abstract fun register(container: PluginContainer)

    override fun execute(src: CommandSource?, args: CommandContext?): CommandResult {
        val result: CommandResult

        try {
            if (!src!!.hasPermission("pumpkin.command.$name.execute")) {
                throw PermissionCommandException("pumpkin.command.$name.execute")
            }

            result = executeCommand(src, args!!)
        } catch (e: Exception) {
            when (e) {
                is PermissionCommandException -> src!!.sendColoredMessage(cmdNoPerms, "perm" to e.permission)
                is InternalCommandException -> src!!.sendColoredMessage(cmdError, "error" to e.friendlyMessage)
                is ArgumentCommandException -> src!!.sendColoredMessage(cmdArgs, "given" to e.given, "expected" to e.paramType)
                is InvalidUsageException -> src!!.sendColoredMessage(cmdUsage, "usage" to e.usage)
                else -> throw e
            }

            return CommandResult.empty()
        }

        return result
    }

    abstract fun executeCommand(src: CommandSource, args: CommandContext): CommandResult

    val game: Game get() = Sponge.getGame()
    val server: Server get() = game.server
}