package com.github.sky295.command

import com.github.sky295.config.Config
import com.github.sky295.util.NBTUtil
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.platform.util.modifyLore

@CommandHeader("ldq")
object LDQCommand {
    @CommandBody
    val set = subCommand {
        dynamic(optional = true) {
            execute<Player> { sender, _, argument ->
                val itemInMainHand = sender.inventory.itemInMainHand
                val split = argument.split(" ")
                val nbtItem = NBTUtil.setDurable(
                    NBTUtil.setRange(NBTUtil.setFlag(itemInMainHand, split[2]), split[1].toInt()), split[0].toInt())
                nbtItem.modifyLore {
                    this.addAll(Config.getLdqLore(nbtItem))
                }
                sender.inventory.setItemInMainHand(nbtItem)
            }
        }
    }
}