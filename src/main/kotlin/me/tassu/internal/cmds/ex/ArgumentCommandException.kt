package me.tassu.internal.cmds.ex

class ArgumentCommandException(var paramType: String, var given: Any) : Exception()