package me.tassu.cmds.meta

import me.tassu.Pumpkin
import me.tassu.cmds.ex.ArgumentCommandException
import me.tassu.cmds.ex.InternalCommandException
import me.tassu.cmds.ex.InvalidUsageException
import me.tassu.cmds.ex.PermissionCommandException
import me.tassu.msg.GeneralMessages
import me.tassu.util.sendMessage
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.plugin.PluginContainer

abstract class PumpkinCommand : CommandExecutor {

    abstract fun register(container: PluginContainer)

    override fun execute(src: CommandSource?, args: CommandContext?): CommandResult {
        val result: CommandResult

        try {
            result = executeCommand(src!!, args!!)
        } catch (e: Exception) {
            when (e) {
                is PermissionCommandException -> src!!.sendMessage(messages.msgNoPermissions, "missingPermission" to e.permission)
                is InternalCommandException -> src!!.sendMessage(messages.msgCommandException, "error" to e.friendlyMessage)
                is ArgumentCommandException -> src!!.sendMessage(messages.msgInvalidArguments, "given" to e.given, "expected" to e.paramType)
                is InvalidUsageException -> src!!.sendMessage(messages.msgInvalidUsage, "usage" to e.usage)
                else -> throw e
            }

            return CommandResult.empty()
        }

        return result
    }

    abstract fun executeCommand(src: CommandSource, args: CommandContext): CommandResult

    val messages: GeneralMessages get() = Pumpkin.messages
    val game: Game get() = Sponge.getGame()

}