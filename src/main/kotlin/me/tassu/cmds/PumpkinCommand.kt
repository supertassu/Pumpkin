package me.tassu.cmds

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.Pumpkin
import me.tassu.internal.cmds.meta.AbstractCommand
import me.tassu.internal.util.kt.sendColoredMessages
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.text.Text

@Singleton
class PumpkinCommand : AbstractCommand("Pumpkin", "pumpkin") {

    override val arguments: Array<CommandElement> = arrayOf(
            GenericArguments.optional(GenericArguments.remainingRawJoinedStrings(Text.of("arguments")))
    )

    @Inject
    private lateinit var pumpkin: Pumpkin

    override fun executeCommand(src: CommandSource, args: CommandContext): CommandResult {
        src.sendColoredMessages(generalMessages.commands.pumpkin,
                "version" to pumpkin.version,
                "enabled modules" to pumpkin.features.filter { it.value.isEnabled }.toList().joinToString { it.first },
                "disabled modules" to pumpkin.features.filter { !it.value.isEnabled }.toList().joinToString { it.first }
        )

        return CommandResult.empty()
    }
}
