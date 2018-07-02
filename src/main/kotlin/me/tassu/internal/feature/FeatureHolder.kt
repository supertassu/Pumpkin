package me.tassu.internal.feature

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.chat.ChatModule
import me.tassu.features.punishments.PunishmentFeature

@Singleton
class FeatureHolder {

    @Inject lateinit var chat: ChatModule
    @Inject lateinit var punishment: PunishmentFeature

}