package me.tassu.internal.cfg

import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import me.tassu.Pumpkin
import me.tassu.Pumpkin.container
import me.tassu.Pumpkin.log
import java.nio.file.Files
import java.nio.file.Path

abstract class Configurable(private val name: String, val prefix: String = name) {

    private val path: Path = Pumpkin.configDir.resolve("$name.conf")
    internal lateinit var config: Config

    init {
        if (!Files.exists(path)) {
            container!!.getAsset("$name.conf").get().copyToFile(path)
            log.debug("-> $name.conf was saved to ${path.toFile().absolutePath}")
        }

        reload()
    }

    internal var lastReload: Long = System.currentTimeMillis()

    inline fun <reified T> provide(key: String): ConfigValueDelegate<T> {
        return ConfigValueDelegate(T::class.java, key)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun reload() {
        config = ConfigFactory.parseString(path.toFile().readText(Charsets.UTF_8)).getConfig("pumpkin.$prefix")
        lastReload = System.currentTimeMillis()
    }

}