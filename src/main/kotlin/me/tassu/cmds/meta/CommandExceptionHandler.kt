package me.tassu.cmds.meta

import co.aikar.commands.*
import me.tassu.cmds.ex.InternalCommandException

object CommandExceptionHandler : ExceptionHandler {
    override fun execute(command: BaseCommand?, registeredCommand: RegisteredCommand<out CommandExecutionContext<*, *>>?, sender: CommandIssuer, args: MutableList<String>?, t: Throwable): Boolean {
        when (t::class.java) {
            InternalCommandException::class.java -> {

            }

            else -> {
                return false
            }
        }

        return true
    }

}
