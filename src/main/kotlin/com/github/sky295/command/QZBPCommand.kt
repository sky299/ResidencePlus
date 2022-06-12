package com.github.sky295.command

import com.github.sky295.config.Config
import com.github.sky295.util.NBTUtil
import org.bukkit.entity.Player
import org.bukkit.inventory.meta.ItemMeta
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.module.nms.getName
import taboolib.platform.util.modifyLore
import taboolib.platform.util.modifyMeta
import taboolib.platform.util.replaceName

@CommandHeader("qzbp")
object QZBPCommand {
    @CommandBody
    val set = subCommand {
        dynamic {
            execute<Player> { sender, _, argument ->
                val itemInMainHand = sender.inventory.itemInMainHand
                val split = argument.split(" ")
                val nbtItem = NBTUtil.setPower(NBTUtil.setRange(NBTUtil.setTime(itemInMainHand,split[3].toInt()), split[2].toInt()), split[0].toInt())
                nbtItem.modifyMeta<ItemMeta> {
                    this.setDisplayName(split[1])
                    this.lore = Config.getQzbpLore(nbtItem)
                }
                sender.inventory.setItemInMainHand(nbtItem)
            }
        }
    }
}