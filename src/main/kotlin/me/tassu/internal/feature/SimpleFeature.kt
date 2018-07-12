package me.tassu.internal.feature

/**
 * Simple implementation of [Feature] handling enabling and disabling.
 */
abstract class SimpleFeature : Feature {

    var enabled = false
    protected set

    override fun enable() {
        enabled = true
    }

    override fun disable() {
        enabled = false
    }

    override val permissionPrefix: String
    get() = "feature.$id"

}