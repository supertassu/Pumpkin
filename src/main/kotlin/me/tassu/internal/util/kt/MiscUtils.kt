package me.tassu.internal.util.kt

import java.util.*

fun <T> T?.toOptional(): Optional<T> {
    return Optional.ofNullable(this)
}