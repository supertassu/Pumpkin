package me.tassu.internal.api.prefix

import com.google.inject.Inject
import org.spongepowered.api.entity.living.player.Player
import me.lucko.luckperms.api.LuckPermsApi
import org.spongepowered.api.Sponge
import me.lucko.luckperms.api.ChatMetaType
import me.tassu.internal.cfg.GeneralMessages

class LuckPermsPrefixProvider : PrefixProvider {

    @Inject
    private lateinit var generalMessages: GeneralMessages

    private val luckPerms by lazy {
        Sponge.getServiceManager().getRegistration(LuckPermsApi::class.java).get().provider
    }

    private fun getChatMeta(player: Player, type: ChatMetaType): String? {
        val possibleUser = luckPerms.getUserSafe(player.uniqueId)

        if (!possibleUser.isPresent) {
            throw Exception("User data was not found.")
        }

        val user = possibleUser.get()

        val userData = user.cachedData
        val metaData = userData.getMetaData(luckPerms.getContextsForPlayer(player))

        return if (type == ChatMetaType.PREFIX) metaData.prefix else metaData.suffix
    }

    private fun format(string: String?, type: ChatMetaType): String {
        return if (type == ChatMetaType.PREFIX) {
            if (string == null) {
                generalMessages.chat.prefixes.none
            } else {
                generalMessages.chat.prefixes.present.replace("{{value}}", string)
            }
        } else {
            if (string == null) {
                generalMessages.chat.suffixes.none
            } else {
                generalMessages.chat.suffixes.present.replace("{{value}}", string)
            }
        }
    }

    override fun providePrefix(player: Player): String {
        return format(getChatMeta(player, ChatMetaType.PREFIX), ChatMetaType.PREFIX)
    }

    override fun provideSuffix(player: Player): String {
        return format(getChatMeta(player, ChatMetaType.SUFFIX), ChatMetaType.SUFFIX)
    }
}