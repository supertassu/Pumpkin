package me.tassu.internal.cfg

import com.typesafe.config.ConfigValueType
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class ConfigValueDelegate<T>(private val clazz: Class<T>, private val name: String) {

    var lastReload = System.currentTimeMillis()

    private var value: T? = null


    @Suppress("UNCHECKED_CAST")
    operator fun provideDelegate(thisRef: Configurable, prop: KProperty<*>): ReadOnlyProperty<Configurable, T> {
        if (this.lastReload > thisRef.lastReload || value == null) {
            this.lastReload = thisRef.lastReload

            val value = thisRef.config.getValue(name)

            when (value.valueType()) {
                ConfigValueType.STRING -> this.value = thisRef.config.getString(name) as T
                ConfigValueType.BOOLEAN -> this.value = thisRef.config.getBoolean(name) as T
                ConfigValueType.LIST -> {
                    this.value = thisRef.config.getStringList(name) as T
                }

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