package me.tassu.internal.util

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.Pumpkin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spongepowered.api.plugin.PluginContainer

@Singleton
class PumpkinLog {

    @Inject private lateinit var container: PluginContainer
    @Inject private lateinit var instance: Pumpkin

    private val logger: Logger get() {
        return container.logger
    }

    private val debug get() = instance.debug

    fun info(string: String) {
        logger.info(string)
    }

    fun debug(string: String, module: String = "") {
        if (!debug) return

        info("[DEBUG${if (module.isNotEmpty()) ": $module]" else "]"} $string")
    }

    fun error(string: String, throwable: Throwable? = null) {
        if (throwable != null) {
            logger.error(string, throwable)
        } else {
            logger.error(string)
        }
    }

    fun warn(string: String) {
        logger.warn(string)
    }
}
