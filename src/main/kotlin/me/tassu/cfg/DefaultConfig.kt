package me.tassu.cfg

import com.uchuhimo.konf.ConfigSpec

object DefaultConfig : ConfigSpec("pumpkin.core") {

    val debug by optional(false)

}