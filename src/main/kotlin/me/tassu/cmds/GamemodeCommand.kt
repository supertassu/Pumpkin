package me.tassu.cmds

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.gamemode.GameMode

@CommandAlias("gamemode|gm")
@CommandPermission("pumpkin.command.gamemode.execute")
object GamemodeCommand : BaseCommand() {

    @HelpCommand
    fun default(@Suppress("UNUSED_PARAMETER")
                sender: CommandSource) {
        TODO("send help message")
    }

    @CommandCompletion("@gamemode @players")
    @Description("Lists all of your or another players residences.")
    fun set(@Suppress("UNUSED_PARAMETER") player: CommandSource,
            mode: GameMode, @Flags("defaultself") target: Player) {
        target.gameMode().set(mode)
        TODO("send success message")
    }

}