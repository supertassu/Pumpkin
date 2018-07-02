package me.tassu.internal.feature

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.chat.ChatModule

@Singleton
class FeatureHolder {

    @Inject lateinit var chat: ChatModule

}