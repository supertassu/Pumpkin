package me.tassu.holders

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.misc.ChatFeature
import me.tassu.features.misc.UserDataFeature
import me.tassu.features.punishments.PunishmentFeature

@Singleton
/**
 * Holds all the [Feature] instances.
 */
class FeatureHolder {

    @Inject lateinit var chat: ChatFeature
    @Inject lateinit var punishment: PunishmentFeature
    @Inject lateinit var userData: UserDataFeature

}