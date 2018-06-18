package me.tassu.cmds.completions

import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.SpongeCommandCompletionContext
import co.aikar.commands.SpongeCommandExecutionContext
import co.aikar.commands.annotation.Optional
import co.aikar.commands.contexts.ContextResolver
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.entity.living.player.gamemode.GameModes

object GameModeCompletion : ContextResolver<GameMode, SpongeCommandExecutionContext> {
    override fun getContext(it: SpongeCommandExecutionContext): GameMode {
        return when (it.popFirstArg()) {
            "c", "crea", "creative" -> GameModes.CREATIVE
            "s", "surv", "survival" -> GameModes.SURVIVAL
            "a", "adv", "adventure" -> GameModes.ADVENTURE
            "sp", "spec", "spectator" -> GameModes.SPECTATOR
            else -> {
                val isOptional = it.hasAnnotation(Optional::class.java)

                if (isOptional) return GameModes.NOT_SET
                throw InvalidCommandArgument("E_USER No game mode was specified.", false)
            }
        }
    }
}