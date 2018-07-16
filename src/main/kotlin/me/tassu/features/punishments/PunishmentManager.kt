package me.tassu.features.punishments

import com.google.inject.Inject
import me.tassu.features.punishments.punishment.Punishment
import me.tassu.features.punishments.punishment.PunishmentType
import me.tassu.internal.db.table.tables.PunishmentsTable
import org.spongepowered.api.util.ban.BanType
import java.net.InetAddress
import java.util.*

class PunishmentManager {

    @Inject private lateinit var punishmentTable: PunishmentsTable

    fun getAllPunishments(): Set<Punishment> {
        return punishmentTable.queryAll()
    }

    fun getAllPunishments(type: BanType): Set<Punishment> {
        return punishmentTable.queryByType(type)
    }

    fun getPunishmentsForUser(uuid: UUID): Set<Punishment> {
        return punishmentTable.queryByTargetUuid(uuid)
    }

    fun getPunishmentsForIp(ip: InetAddress): Set<Punishment> {
        return punishmentTable.queryByTargetIp(ip)
    }

    fun revokePunishment(punishment: Punishment,
                         reason: String = "Punishment revoked by an operator",
                         actor: UUID = PunishmentFeature.CONSOLE_UUID): Punishment {
        return punishmentTable.revokePunishment(punishment, reason, actor)
    }

    fun banUser(target: UUID, actor: UUID, reason: String, expiresOn: Long?) {
        punishmentTable.createPunishmentForUser(PunishmentType.BAN, target, actor, reason, expiresOn)
    }

}