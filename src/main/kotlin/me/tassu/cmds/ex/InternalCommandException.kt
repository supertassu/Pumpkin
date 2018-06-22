package me.tassu.cmds.ex

class InternalCommandException(@Suppress("MemberVisibilityCanBePrivate") val code: String, val friendlyMessage: String = "Internal exception: $code") : Exception(friendlyMessage)