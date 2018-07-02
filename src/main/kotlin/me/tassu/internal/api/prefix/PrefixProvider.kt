package me.tassu.internal.api.prefix

import com.google.inject.Inject
import me.tassu.internal.cfg.GeneralMessages
import org.spongepowered.api.entity.living.player.Player

interface PrefixProvider {

    fun providePrefix(player: Player): String

    fun provideSuffix(player: Player): String

    class DummyPrefixProvider : PrefixProvider {

        @Inject
        private lateinit var generalMessages: GeneralMessages

        override fun provideSuffix(player: Player): String {
            return generalMessages.chat.suffixes.none
        }

        override fun providePrefix(player: Player): String {
            return generalMessages.chat.prefixes.none
        }
    }

}