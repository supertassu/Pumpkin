package me.tassu.cmds.meta

import me.tassu.cmds.ex.ArgumentCommandException
import me.tassu.cmds.ex.InternalCommandException
import me.tassu.cmds.ex.InvalidUsageException
import me.tassu.cmds.ex.PermissionCommandException
import me.tassu.msg.GeneralMessages.cmdArgs
import me.tassu.msg.GeneralMessages.cmdError
import me.tassu.msg.GeneralMessages.cmdUsage
import me.tassu.msg.GeneralMessages.noPerms
import me.tassu.util.sendMessage
import org.spongepowered.api.Game
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
                is PermissionCommandException -> src!!.sendMessage(noPerms, "perm" to e.permission)
                is InternalCommandException -> src!!.sendMessage(cmdError, "error" to e.friendlyMessage)
                is ArgumentCommandException -> src!!.sendMessage(cmdArgs, "given" to e.given, "expected" to e.paramType)
                is InvalidUsageException -> src!!.sendMessage(cmdUsage, "usage" to e.usage)
                else -> throw e
            }

            return CommandResult.empty()
        }

        return result
    }

    abstract fun executeCommand(src: CommandSource, args: CommandContext): CommandResult

    val game: Game get() = Sponge.getGame()

}