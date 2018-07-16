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

    val isEnabled: Boolean

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
     */
    val permissions: List<String>

    /**
     * Used to override the prefix of the feature permissions.
     */
    val permissionPrefix: String

    /**
     * List of all other [Feature]s this module requires.
     */
    val dependencies: List<String>

}