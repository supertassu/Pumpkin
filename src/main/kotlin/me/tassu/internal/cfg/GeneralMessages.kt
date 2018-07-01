package me.tassu.internal.cfg

object GeneralMessages : Configurable("messages") {

    // general
    val msgPrefix by provide<String>("meta.prefix")

    // chat related
    val prefixYes by provide<String>("chat.prefix.present")
    val prefixNo by provide<String>("chat.prefix.none")
    val suffixYes by provide<String>("chat.suffix.present")
    val suffixNo by provide<String>("chat.suffix.none")
    val chatFormat by provide<String>("chat.format")
    
    // general command related
    val cmdNoPerms by provide<String>("commands.no permissions")
    val cmdUsage by provide<String>("commands.usage")
    val cmdArgs by provide<String>("commands.args")
    val cmdError by provide<String>("commands.error")

    // /gamemode related
    val cmdGmSetOwn by provide<String>("commands.gamemode.msg self own")
    val cmdGmSetOther by provide<String>("commands.gamemode.msg self other")
    val cmdOtherGmSetOwn by provide<String>("commands.gamemode.msg others own")
    val cmdOtherGmSetOther by provide<String>("commands.gamemode.msg others other")

}
