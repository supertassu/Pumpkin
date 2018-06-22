package me.tassu.cmds

import me.tassu.cmds.completions.GameModeCompletion
import me.tassu.cmds.ex.InvalidUsageException
import me.tassu.cmds.meta.PumpkinCommand
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.Text

object GamemodeCommand : PumpkinCommand() {

    override fun executeCommand(src: CommandSource, args: CommandContext): CommandResult {
        val player = args.getOne<Player>("player")
        val mode = args.getOne<GameMode>("mode")

        if (!mode.isPresent || !player.isPresent) {
            throw InvalidUsageException("/gamemode <mode> [player]")
        }

        player.get().gameMode().set(mode.get())
        return CommandResult.affectedEntities(1)
    }

    override fun register(container: PluginContainer) {
        val spec = CommandSpec.builder()
                .description(Text.of("Game mode"))
                .arguments(
                        GenericArguments.onlyOne(GameModeCompletion("mode")),
                        GenericArguments.onlyOne(GenericArguments.playerOrSource(Text.of("player")))
                ).executor(this)
                .build()

        Sponge.getCommandManager().register(container, spec, "gamemode", "gm")

    }
}