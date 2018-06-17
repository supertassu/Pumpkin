package me.tassu.msg

import com.uchuhimo.konf.ConfigSpec

object GeneralMessages : ConfigSpec("pumpkin.messages.meta") {

    val generalPrefix by optional("&9(Pumpkin) ")

    // colors
    val text by optional("&7")
    val highlight by optional("&r")

}