package me.tassu.internal.di

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.name.Named
import me.tassu.Pumpkin
import ninja.leaping.configurate.commented.CommentedConfigurationNode
import ninja.leaping.configurate.hocon.HoconConfigurationLoader
import ninja.leaping.configurate.loader.ConfigurationLoader
import org.spongepowered.api.Game
import org.spongepowered.api.Server
import org.spongepowered.api.Sponge
import org.spongepowered.api.plugin.PluginContainer
import java.nio.file.Files
import java.nio.file.Path

class PumpkinModule(private val container: PluginContainer, private val pumpkin: Pumpkin, private val configDir: Path) : AbstractModule() {

    fun createInjector(): Injector {
        return Guice.createInjector(this)
    }

    override fun configure() {
        // plugin instance etc
        this.bind(PluginContainer::class.java).toInstance(container)
        this.bind(Pumpkin::class.java).toInstance(pumpkin)

        // sponge stuff
        this.bind(Game::class.java).toInstance(Sponge.getGame())
        this.bind(Server::class.java).toInstance(Sponge.getServer())

        // config dir
        this.bind(Path::class.java).toInstance(configDir)
    }

    @Provides @Named("pumpkin") internal fun providePumpkinConfigurationLoader(): ConfigurationLoader<CommentedConfigurationNode> {
        return provideConfigurationLoader("pumpkin")
    }

    @Provides @Named("messages") internal fun provideMessagesConfigurationLoader(): ConfigurationLoader<CommentedConfigurationNode> {
        return provideConfigurationLoader("messages")
    }

    private fun provideConfigurationLoader(fileName: String): ConfigurationLoader<CommentedConfigurationNode> {
        if (Files.notExists(configDir)) {
            Files.createDirectory(configDir)
        }

        val file = configDir.resolve("$fileName.conf")

        if (Files.notExists(file)) {
            Files.createFile(file)
        }

        return HoconConfigurationLoader.builder().setPath(file).build()
    }

}