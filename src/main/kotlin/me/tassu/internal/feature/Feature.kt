package me.tassu.internal.feature

interface Feature {

    fun enable()
    fun disable()

    val listeners: List<Any>
}