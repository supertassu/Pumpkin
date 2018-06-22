package me.tassu.cmds

import co.aikar.commands.annotation.*
import me.tassu.cmds.meta.PumpkinCommand
import me.tassu.util.sendMessage
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode

@CommandAlias("gamemode|gm")
@CommandPermission("pumpkin.command.gamemode.execute")
object GamemodeCommand : PumpkinCommand() {

    @HelpCommand
    fun default(sender: CommandSource) {
        sender.sendMessage(messages.msgInvalidUsage, "usage" to "/gamemode <mode> [player]")
    }

    @CommandCompletion("@gamemode @players")
    @Description("Lists all of your or another players residences.")
    fun set(@Suppress("UNUSED_PARAMETER") player: CommandSource,
            mode: GameMode, @Flags("defaultself") target: Player) {
        target.gameMode().set(mode)
        TODO("send success message")
    }

}