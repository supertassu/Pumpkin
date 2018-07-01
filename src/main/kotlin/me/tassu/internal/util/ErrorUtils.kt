package me.tassu.internal.util

@JvmName("throws")
fun (() -> Any).throws(vararg target: Class<out Throwable>): Boolean {
    try {
        this()
    } catch (e: Throwable) {
        val ex = e::class.java
        if (target.isEmpty()) return true
        return target.stream().anyMatch { ex.isAssignableFrom(it) }
    }

    return false
}

@JvmName("doesNotThrow")
fun (() -> Any).doesNotThrow(vararg target: Class<out Throwable>): Boolean {
    return !this.throws(*target)
}