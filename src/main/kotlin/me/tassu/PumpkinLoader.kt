package me.tassu

import com.google.inject.Inject
import me.tassu.internal.di.PumpkinHolder
import me.tassu.internal.di.PumpkinModule
import org.spongepowered.api.Sponge
import org.spongepowered.api.config.ConfigDir
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.game.state.GameInitializationEvent
import org.spongepowered.api.plugin.Dependency
import org.spongepowered.api.plugin.Plugin
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Path


/**
 * This is the entry point of Pumpkin.
 * @author tassu <git@tassu.me>
 */
@Plugin(
        id = "pumpkin",
        name = "Pumpkin",
        dependencies = [
            Dependency(
                    id = "luckperms",
                    optional = true
            )
        ]
)
class PumpkinLoader {

    @Inject
    private lateinit var container: PluginContainer

    @Inject
    @ConfigDir(sharedRoot = false)
    private lateinit var configDir: Path

    @Listener
    fun init(event: GameInitializationEvent) {
        val pumpkin = Pumpkin()
        val injector = PumpkinModule(container, pumpkin, configDir).createInjector()

        injector.injectMembers(pumpkin)
        injector.injectMembers(PumpkinHolder())

        Sponge.getEventManager().registerListeners(this, pumpkin)
    }

}
