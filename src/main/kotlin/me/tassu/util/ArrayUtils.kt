package me.tassu.util

import java.util.stream.Stream

fun <T> Array<T>.stream(): Stream<T> {
    return this.toList().stream()
}