package me.tassu.features.punishments

import com.google.inject.Inject
import me.tassu.features.punishments.ban.PumpkinBan
import me.tassu.internal.db.table.tables.PunishmentsTable
import java.util.*

class PunishmentManager {

    @Inject private lateinit var punishmentTable: PunishmentsTable

    fun getPunishmentsForUser(uuid: UUID): Set<Punishment> {
        val set = mutableSetOf<Punishment>()
        val result = punishmentTable.queryByTargetUuid(uuid)

        while (result.next()) {
            set.add(when (result.getString("type")) {
                "ban" -> PumpkinBan(result)
                else -> {
                    throw IllegalArgumentException("FAIL: Punishment type ${result.getString("type")} is not supported.")
                }
            })
        }

        return set
    }

}