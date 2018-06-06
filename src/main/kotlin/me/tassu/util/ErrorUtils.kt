package me.tassu.util

@JvmName("throws")
fun (() -> Any).throws(target: Class<out Throwable>? = null): Boolean {
    try {
        this()
    } catch (e: Throwable) {
        if (target == null) return true
        return e.javaClass.isAssignableFrom(target)
    }

    return false
}

@JvmName("doesNotThrow")
fun (() -> Any).doesNotThrow(target: Class<out Throwable>? = null): Boolean {
    return !this.throws(target)
}