package me.tassu.internal.api.prefix

import me.tassu.internal.cfg.GeneralMessages
import org.spongepowered.api.entity.living.player.Player

interface PrefixProvider {

    fun providePrefix(player: Player): String {
        return GeneralMessages.prefixNo
    }

    fun provideSuffix(player: Player): String {
        return GeneralMessages.suffixNo
    }

    class DummyPrefixProvider : PrefixProvider

}