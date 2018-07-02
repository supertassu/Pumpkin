package me.tassu.features.punishments.ban

import me.tassu.features.punishments.AbstractPunishment
import java.sql.ResultSet

class PumpkinBan(resultSet: ResultSet) : AbstractPunishment(resultSet) {

    init {
        if (resultSet.getString("type") != "ban") {
            throw IllegalArgumentException("FAIL: Punishment type ${resultSet.getString("type")} is not a ban.")
        }
    }

}