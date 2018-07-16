package me.tassu.features.punishments.punishment

import java.net.InetAddress
import java.util.*

interface Punishment {

    val id: Int

    val type: PunishmentType
    val actor: UUID

    /* BOTH targetUuid AND targetIp SHOULD NOT BE NULL */
    val targetUuid: UUID?
    val targetIp: InetAddress?

    val date: Long
    val expiresOn: Long?

    val reason: String

    val revokedBy: UUID?
    val revokedOn: Long?
    val revokeReason: String?


}