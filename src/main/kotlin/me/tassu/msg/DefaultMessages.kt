package me.tassu.msg

enum class DefaultMessages(val id: String, vararg var message: String) {

    PERMISSION_DENIED("command.general.permission denied",
            "\${pumpkin.messages.meta.prefix}\${pumpkin.messages.meta.text}You do not have permission do this."),

    ;

    val internal = Message(id, message.toList())

    companion object {
        fun reload() {
            values().forEach {
                MessageManager.register(it.internal)
            }
        }
    }
}