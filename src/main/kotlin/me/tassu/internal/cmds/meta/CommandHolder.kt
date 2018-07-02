package me.tassu.internal.cmds.meta

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.cmds.GamemodeCommand

@Singleton
class CommandHolder {

    @Inject lateinit var gameModeCommand: GamemodeCommand

}