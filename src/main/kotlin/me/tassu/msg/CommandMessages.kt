package me.tassu.msg

import com.uchuhimo.konf.ConfigSpec

object CommandMessages : ConfigSpec("pumpkin.messages.commands.general") {

    val noPermission by optional("\${pumpkin.messages.meta.prefix}\${pumpkin.messages.meta.text}You do not have permission do this.")

}