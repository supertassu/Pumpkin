package me.tassu.features.punishments.cmd

import com.google.inject.Inject
import com.google.inject.Singleton
import me.tassu.features.punishments.PunishmentFeature
import me.tassu.features.punishments.PunishmentManager
import me.tassu.features.punishments.ban.PumpkinBan
import me.tassu.features.punishments.punishment.PunishmentType
import me.tassu.internal.cmds.ex.InvalidUsageException
import me.tassu.internal.cmds.meta.AbstractCommand
import me.tassu.internal.util.kt.formatColoredMessage
import me.tassu.internal.util.kt.getAllMessageReceiversWithPermission
import me.tassu.internal.util.kt.sendColoredMessage
import me.tassu.internal.util.kt.text
import org.spongepowered.api.command.CommandResult
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.command.args.CommandContext
import org.spongepowered.api.command.args.CommandElement
import org.spongepowered.api.command.args.GenericArguments
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.entity.living.player.User
import org.spongepowered.api.plugin.PluginContainer

@Singleton
class PardonCommand : AbstractCommand("Pardon", "pardon", "unban") {
    override val arguments: Array<CommandElement> = arrayOf(
            GenericArguments.onlyOne(GenericArguments.optional(GenericArguments.user("target".text()))),
            GenericArguments.optional(GenericArguments.remainingJoinedStrings("reason".text()))
    )

    @Inject private lateinit var punishmentManager: PunishmentManager
    @Inject private lateinit var pluginContainer: PluginContainer

    override fun executeCommand(src: CommandSource, args: CommandContext): CommandResult {
        val rawTarget = args.getOne<User>("target")
        val rawReason = args.getOne<String>("reason")

        if (!rawTarget.isPresent) {
            throw InvalidUsageException("/pardon <user> [reason]")
        }

        val reason = if (rawReason.isPresent) rawReason.get() else "Punishment revoked by an operator"
        val target = rawTarget.get()

        val uuid = if (src is Player) {
            src.uniqueId
        } else {
            PunishmentFeature.CONSOLE_UUID
        }

        game.scheduler.createTaskBuilder()
                .delayTicks(1)
                .name("/pardon command executor")
                .async()
                .execute { _ ->
                    val ban = punishmentManager
                            .getPunishmentsForUser(target.uniqueId)
                            .filter { it.type == PunishmentType.BAN }
                            .map { it as PumpkinBan.Uuid }
                            .firstOrNull { it.hasNotExpired() }

                    if (ban == null) {
                        src.sendColoredMessage(generalMessages.punishments.commands.pardonBanNotFound,
                                "target" to target.name)
                        return@execute
                    }

                    punishmentManager.revokePunishment(ban, reason, uuid)

                    src.sendColoredMessage(generalMessages.punishments.commands.pardonedSelf,
                            "target" to target.name,
                            "reason" to reason)

                    val message = generalMessages.punishments.commands.pardonedOther.formatColoredMessage(
                            "actor" to src.name,
                            "reason" to reason,
                            "target" to target.name)

                    server.getAllMessageReceiversWithPermission("pumpkin.features.punishments.commands.pardon.view")
                            .filter { it != src }
                            .forEach { it.sendMessage(message) }

                }
                .submit(pluginContainer)

        return CommandResult.success()
    }

    override val permissionPrefix: String = "features.punishments.commands.$name"
    override val permissions: List<String> = listOf(*super.permissions.toTypedArray(), "view")
}