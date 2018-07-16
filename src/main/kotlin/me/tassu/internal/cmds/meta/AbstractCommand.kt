package me.tassu.internal.cmds.meta

import me.tassu.internal.cfg.GeneralMessages
import me.tassu.internal.cmds.ex.InternalCommandException
import me.tassu.internal.cmds.ex.InvalidUsageException
import me.tassu.internal.cmds.ex.PermissionCommandException
import me.tassu.internal.di.PumpkinHolder
import me.tassu.internal.feature.SimpleFeature
import me.tassu.internal.util.kt.sendColoredMessage
import org.spongepowered.api.Game
import org.spongepowered.api.Server
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.text.Text
import java.lang.IllegalArgumentException

abstract class AbstractCommand(private val display: String, private vararg val names: String) : SimpleFeature(), CommandExecutor {

    protected val generalMessages: GeneralMessages get() = PumpkinHolder.getInstance().messages

    init {
        if (names.isEmpty()) {
            throw IllegalArgumentException("Names is empty.")
        }
    }

    val name = names.first()

    override val id: String = "cmd_$name"

    override val permissionPrefix: String
        get() = "command.$name"

    abstract val arguments: Array<CommandElement>

    override fun enable() {
        super.enable()

        val spec = CommandSpec.builder()
                .description(Text.of(display))
                .arguments(*arguments)
                .executor(this)
                .build()

        Sponge.getCommandManager().register(PumpkinHolder.getInstance().container, spec, *names)
    }

    override fun execute(src: CommandSource?, args: CommandContext?): CommandResult {
        val result: CommandResult

        try {
            if (!src!!.hasPermission("pumpkin.$permissionPrefix.execute")) {
                throw PermissionCommandException("pumpkin.$permissionPrefix.execute")
            }

            result = executeCommand(src, args!!)
        } catch (e: Exception) {
            when (e) {
                is PermissionCommandException -> src!!.sendColoredMessage(generalMessages.commands.meta.noPerms, "perm" to e.permission)
                is InternalCommandException -> src!!.sendColoredMessage(generalMessages.commands.meta.error, "error" to e.friendlyMessage)
                is InvalidUsageException -> src!!.sendColoredMessage(generalMessages.commands.meta.usage, "usage" to e.usage)
                else -> throw e
            }

            return CommandResult.empty()
        }

        return result
    }

    abstract fun executeCommand(src: CommandSource, args: CommandContext): CommandResult

    override val listeners: List<Any> = listOf()
    override val permissions: List<String> = listOf("execute")
    override val dependencies: List<String> = listOf()

    val game: Game get() = Sponge.getGame()
    val server: Server get() = game.server
}