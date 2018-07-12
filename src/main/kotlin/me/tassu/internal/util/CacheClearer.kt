package me.tassu.internal.util

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.Pumpkin

@Singleton
class CacheClearer : Runnable {

    @Inject private lateinit var pumpkin: Pumpkin

    override fun run() {
        pumpkin.features.forEach { _, it -> it.clearCache() }
    }

}