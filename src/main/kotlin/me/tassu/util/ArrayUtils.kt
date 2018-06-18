package me.tassu.util

import java.util.stream.Stream

fun <T> Array<T>.stream(): Stream<T> {
    return this.toList().stream()
}

fun <E> MutableList<E>.pop(): E {
    val returnable = this.first()
    this.removeAt(0)
    return returnable
}