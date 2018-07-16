package me.tassu.holders

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.cmds.FlyCommand
import me.tassu.cmds.GamemodeCommand
import me.tassu.cmds.PumpkinCommand
import me.tassu.cmds.TeleportCommand

@Singleton
class CommandHolder {

    @Inject lateinit var gameModeCommand: GamemodeCommand
    @Inject lateinit var teleportCommand: TeleportCommand
    @Inject lateinit var pumpkinCommand: PumpkinCommand
    @Inject lateinit var flightCommand: FlyCommand

}