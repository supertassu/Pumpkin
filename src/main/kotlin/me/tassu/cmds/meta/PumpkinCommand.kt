package me.tassu.cmds.meta

import co.aikar.commands.BaseCommand
import me.tassu.Pumpkin
import me.tassu.msg.GeneralMessages
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge

open class PumpkinCommand : BaseCommand() {

    val messages: GeneralMessages get() = Pumpkin.messages
    val game: Game get() = Sponge.getGame()

}