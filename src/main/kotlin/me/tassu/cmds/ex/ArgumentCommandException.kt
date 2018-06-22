package me.tassu.cmds.ex

import co.aikar.commands.InvalidCommandArgument

class ArgumentCommandException(var paramType: String, var given: Any) : InvalidCommandArgument()