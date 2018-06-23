package me.tassu.cmds.completions

import org.spongepowered.api.entity.living.player.gamemode.GameMode

class PossibleContainer<T>(private val it: T?, private val other: String? = it.toString()) {

    init {
        if (it == null && other == null) throw IllegalArgumentException("specify at least one plz")
    }

    fun isPresent(): Boolean = it != null
    fun get(): T? = it
    fun orElse(): String = other ?: it.toString()

}

fun <T> T.wrap(): PossibleContainer<T>? {
    return PossibleContainer(this)
}
