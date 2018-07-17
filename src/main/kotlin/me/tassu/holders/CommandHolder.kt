package me.tassu.holders

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.cmds.*

@Singleton
class CommandHolder {

    @Inject lateinit var gameModeCommand: GamemodeCommand
    @Inject lateinit var teleportCommand: TeleportCommand
    @Inject lateinit var pumpkinCommand: PumpkinCommand
    @Inject lateinit var flightCommand: FlyCommand
    @Inject lateinit var healCommand: HealCommand
    @Inject lateinit var feedCommand: FeedCommand

}