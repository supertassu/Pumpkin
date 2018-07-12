package me.tassu.internal.cmds.meta

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.cmds.GamemodeCommand
import me.tassu.cmds.TeleportCommand

@Singleton
class CommandHolder {

    @Inject lateinit var gameModeCommand: GamemodeCommand
    @Inject lateinit var teleportCommand: TeleportCommand

}