package me.tassu.internal.util.kt

import org.spongepowered.api.Server
import org.spongepowered.api.text.channel.MessageReceiver

fun Server.getAllMessageReceiversWithPermission(permission: String, includeConsole: Boolean = true): Set<MessageReceiver> {
    val set = onlinePlayers.filter { it.hasPermission(permission) }.toMutableSet<MessageReceiver>()
    if (includeConsole) { set.add(console) }
    return set
}