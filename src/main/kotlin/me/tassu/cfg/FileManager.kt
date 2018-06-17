package me.tassu.cfg

import me.tassu.Pumpkin
import me.tassu.util.stream
import org.spongepowered.api.asset.Asset
import java.io.File
import java.lang.IllegalStateException

object FileManager {

    private val handled = mutableMapOf<String, Asset>()
    private lateinit var dir: File

    fun handle(vararg configs: String) {
        val plugin = Pumpkin.container
        val instance = plugin.instance.orElseThrow({ IllegalStateException("wtf") }) as Pumpkin
        val dir = instance.configDir!!

        if (!dir.toFile().exists()) {
            dir.toFile().mkdir()
        }

        FileManager.dir = dir.toFile()

        configs.stream()
                .filter { !handled.containsKey(it) }
                .forEach {
                    val asset = plugin.getAsset(it).orElseThrow { IllegalArgumentException("$it is not an asset nurd") }
                    asset.copyToDirectory(dir, false, true)
                    handled[it] = asset
                }
    }

    fun file(name: String): File {
        return File(dir, name)
    }

    fun asset(name: String): Asset? {
        return handled[name]
    }

}