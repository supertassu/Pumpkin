package me.tassu.internal.cmds.completions

import me.tassu.internal.util.kt.text
import org.spongepowered.api.Game
import org.spongepowered.api.Sponge
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandArgs
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.util.Tristate
import java.lang.IllegalArgumentException

class TriStateCompletion(key: String, private var default: Tristate? = Tristate.UNDEFINED) : CommandElement(key.text()) {

    override fun complete(src: CommandSource?, args: CommandArgs?, context: CommandContext?): MutableList<String> {
        return Tristate.values().map { it.name.replace("UNDEFINED", "TOGGLE") }.toMutableList()
    }

    override fun parseValue(src: CommandSource?, args: CommandArgs?): PossibleContainer<Tristate>? {
        var arg = args!!.next()

        if (arg.toUpperCase() == "TOGGLE") {
            arg = "UNDEFINED"
        }

        if (arg.trim().isEmpty()) {
            return if (default != null) {
                PossibleContainer(default)
            } else {
                PossibleContainer(null, "<none>")
            }
        }

        return try {
            PossibleContainer(Tristate.valueOf(arg.toUpperCase()))
        } catch (e: IllegalArgumentException) {
            PossibleContainer(null, arg)
        }
    }

}

