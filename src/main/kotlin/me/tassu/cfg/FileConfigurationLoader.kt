package me.tassu.cfg

import com.typesafe.config.ConfigFactory
import me.tassu.Pumpkin
import me.tassu.util.PumpkinLog
import me.tassu.util.readAsString
import java.io.File

object FileConfigurationLoader {

    private val plugin = Pumpkin.instance
    private val folder: File get() = plugin.dataFolder

    fun load(name: String): Configuration {

        PumpkinLog.debug("loading $name", "config")

        if (!folder.exists()) {
            PumpkinLog.debug("creating folder $folder", "config")
            folder.mkdir()
        }

        val file = File(folder, "$name.conf")

        if (!file.exists()) {
            PumpkinLog.debug("saving default file $file", "config")
            plugin.saveResource("$name.conf", false)
        }

        PumpkinLog.debug("parsing config $name", "config")
        return Configuration(ConfigFactory.parseFile(file)
                .withFallback(ConfigFactory.parseString(plugin.getResource("$name.conf").readAsString())))
    }

}