package me.tassu.cmds.ex

import co.aikar.commands.InvalidCommandArgument

class InternalCommandException(code: String, friendlyMessage: String = "Internal exception: $code") : InvalidCommandArgument()