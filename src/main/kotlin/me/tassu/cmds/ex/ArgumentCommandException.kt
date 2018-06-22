package me.tassu.cmds.ex

class ArgumentCommandException(var paramType: String, var given: Any) : Exception()