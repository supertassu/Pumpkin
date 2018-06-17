package me.tassu.cmds

import me.tassu.cmds.args.Arguments
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.spec.CommandExecutor
import org.spongepowered.api.plugin.PluginContainer
import org.spongepowered.api.command.args.GenericArguments.plugin
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.command.spec.CommandSpec
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.player.gamemode.GameModes
import org.spongepowered.api.text.Text


object GamemodeCommand : CommandExecutor {

    fun init(plugin: PluginContainer) {
        val spec = CommandSpec.builder()
                .description(Text.of("Change a player's game mode"))
                .arguments(
                        Arguments.GameModeArgument(Text.of("mode")),
                        GenericArguments.playerOrSource(Text.of("target"))
                )
                .permission("pumpkin.command.gamemopde.execute")
                .executor(this)
                .build()

        Sponge.getCommandManager().register(plugin, spec, "gamemode", "gm", "gmode", "gamem")

    }

    override fun execute(src: CommandSource?, args: CommandContext?): CommandResult {
        val target = args!!.getOne<Player>("target").get()
        val mode = args.getOne<GameMode>("mode").get()

        if (mode == GameModes.NOT_SET) {
            return CommandResult.empty()
        }

        return CommandResult.success()
    }
}