package me.tassu.msg

import me.tassu.cfg.Configurable

object GeneralMessages : Configurable("messages") {
    
    val chatFormat by provide<String>("chat.format")
    
    val msgPrefix by provide<String>("meta.prefix")
    val noPerms by provide<String>("commands.no permissions")
    val cmdUsage by provide<String>("commands.usage")
    val cmdArgs by provide<String>("commands.args")
    val cmdError by provide<String>("commands.error")


}
