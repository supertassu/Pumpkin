package me.tassu.cfg

object MainConfig : Configurable("pumpkin", "core") {

    val debug by provide<Boolean>("debug")
    val enabledCommands by provide<List<String>>("enabled commands")

}