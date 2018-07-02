package me.tassu.features.punishments

import java.util.*

interface Punishment {

    val type: PunishmentType
    val target: UUID
    val actor: UUID

    val date: Long
    val expiresOn: Long?

    val reason: String

    val revokedBy: UUID?
    val revokedOn: Long?
    val revokeReason: String?


}