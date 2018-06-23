package me.tassu.cfg

import org.spongepowered.api.text.Text
import org.spongepowered.api.text.serializer.TextSerializers
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ConfigValueDelegate<T>(private val clazz: Class<T>, private val name: String) {

    var lastReload = System.currentTimeMillis()

    private var value: T? = null


    @Suppress("UNCHECKED_CAST")
    operator fun provideDelegate(thisRef: Configurable, prop: KProperty<*>): ReadOnlyProperty<Configurable, T> {
        if (this.lastReload > thisRef.lastReload || value == null) {
            this.lastReload = thisRef.lastReload

            when {
                clazz.isAssignableFrom(String::class.java) -> this.value = thisRef.config.getString(name) as T
                clazz.isAssignableFrom(Boolean::class.java) -> this.value = thisRef.config.getBoolean(name) as T
                clazz.isAssignableFrom(List::class.java) -> {
                    val type = clazz.typeParameters[0]
                    if (type != String::class.java) {
                        throw IllegalArgumentException("$type is not valid list type parameter")
                    }
                    this.value = thisRef.config.getStringList(name) as T
                }
                clazz.isAssignableFrom(Text::class.java) -> this.value = TextSerializers.FORMATTING_CODE.deserialize(thisRef.config.getString(name)) as T
                else -> throw IllegalArgumentException("$clazz is not valid config object type")
            }

        }

        return ConfigValue(this)
    }

    class ConfigValue<T>(private val delegate: ConfigValueDelegate<T>) : ReadOnlyProperty<Configurable, T> {
        override fun getValue(thisRef: Configurable, property: KProperty<*>): T {
            return delegate.value!!
        }
    }

}