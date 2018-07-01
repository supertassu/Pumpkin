package me.tassu.internal.api.prefix

import org.spongepowered.api.entity.living.player.Player
import me.lucko.luckperms.api.LuckPermsApi
import org.spongepowered.api.Sponge
import me.lucko.luckperms.api.ChatMetaType
import me.tassu.internal.cfg.GeneralMessages

class LuckPermsPrefixProvider : PrefixProvider {

    private val luckPerms: LuckPermsApi

    init {
        val provider = Sponge.getServiceManager().getRegistration(LuckPermsApi::class.java)
        luckPerms = provider.get().provider
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
                GeneralMessages.prefixNo
            } else {
                GeneralMessages.prefixYes.replace("{{value}}", string)
            }
        } else {
            if (string == null) {
                GeneralMessages.suffixNo
            } else {
                GeneralMessages.suffixYes.replace("{{value}}", string)
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