package me.tassu.cmds.completions

import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.MessageKeys
import co.aikar.commands.SpongeCommandExecutionContext
import co.aikar.commands.annotation.Optional
import co.aikar.commands.contexts.ContextResolver
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.entity.living.player.Player

object PlayerCompletion : ContextResolver<Player?, SpongeCommandExecutionContext> {

    private val game: Game get() = Sponge.getGame()

    override fun getContext(c: SpongeCommandExecutionContext): Player? {
        val isOptional = c.hasAnnotation(Optional::class.java)
        val sender = c.source
        val arg = c.popFirstArg()

        if (arg == null || arg.isEmpty()) {
            if (c.hasFlag("defaultself") && sender is Player) {
                return sender
            }

            if (isOptional) return null
            throw InvalidCommandArgument(MessageKeys.NOT_ALLOWED_ON_CONSOLE, false)
        }

        if (!game.isServerAvailable) throw InvalidCommandArgument("E_INTERNAL SERVER_NOT_AVAILABLE", false)
        val server = game.server

        return server.getPlayer(arg).orElse(null)

    }
}