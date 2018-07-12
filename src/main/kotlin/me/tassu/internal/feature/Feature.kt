package me.tassu.internal.feature

/**
 * Represents one feature, eg. chat.
 * @see FeatureHolder
 */
interface Feature {

    /**
     * Unique identifier of this feature.
     */
    val id: String

    /**
     * Called when this feature is enabled.
     */
    fun enable()

    /**
     * Called when this feature is disabled.
     */
    fun disable()

    /**
     * Called by the cache cleaner every minute.
     */
    fun clearCache() {}

    /**
     * List of all [org.spongepowered.api.event.Listener]s that this module requires.
     * PLEASE NOTE: They are always registered, use [enable] and [disable] to control their state.
     */
    val listeners: List<Any>

    /**
     * List of all permissions this module requires.
     * A prefix of `pumpkin.feature.(FEATURE ID).` will be added.
     *
     * Examples (feature name "example")
     *  * `coke` -> `pumpkin.feature.example.coke`
     *  * `fruit.banana` -> `pumpkin.feature.example.fruit.banana`
     */
    val permissions: List<String>

    /**
     * List of all other [Feature]s this module requires.
     */
    val dependencies: List<String>

}