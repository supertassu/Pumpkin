package me.tassu.internal.util.kt

@JvmName("isMethodPresent")
fun Any.containsMethod(id: String): Boolean {
    val clazz = this::class.java

    return clazz.methods.stream().anyMatch { it.name == id }
}