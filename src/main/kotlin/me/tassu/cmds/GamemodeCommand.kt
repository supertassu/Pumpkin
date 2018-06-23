package me.tassu.cmds

import me.tassu.cmds.completions.GameModeCompletion
import me.tassu.cmds.completions.PlayerCompletion
import me.tassu.cmds.completions.PossibleContainer
import me.tassu.cmds.ex.ArgumentCommandException
import me.tassu.cmds.ex.InvalidUsageException
import me.tassu.cmds.meta.PumpkinCommand
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.data.key.Keys
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.text.Text

object GamemodeCommand : PumpkinCommand() {

    override fun executeCommand(src: CommandSource, args: CommandContext): CommandResult {
        val rawPlayer = args.getOne<PossibleContainer<Player>>("player")
        val rawMode = args.getOne<PossibleContainer<GameMode>>("mode")

        if (!rawMode.isPresent || !rawPlayer.isPresent) {
            throw InvalidUsageException("/gamemode <mode> [player]")
        }

        val mode = rawMode.get()
        val player = rawPlayer.get()

        if (!mode.isPresent()) {
            throw ArgumentCommandException("Game Mode", mode.orElse())
        }

        if (!player.isPresent()) {
            throw ArgumentCommandException("Player", mode.orElse())
        }

        player.get()!!.sendMessage(Text.of("setting ur gm to ${mode.get()}"))
        player.get()!!.offer(Keys.GAME_MODE, mode.get()!!)
        return CommandResult.affectedEntities(1)
    }

    override fun register(container: PluginContainer) {
        val spec = CommandSpec.builder()
                .description(Text.of("Game mode"))
                .arguments(
                        GenericArguments.onlyOne(GenericArguments.optional(GameModeCompletion("mode"))),
                        GenericArguments.onlyOne(GenericArguments.optional(PlayerCompletion("player", true)))
                ).executor(this)
                .build()

        Sponge.getCommandManager().register(container, spec, "gamemode", "gm")

    }
}