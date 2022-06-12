package com.github.sky295.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader("rp")
object MainCommand {

    @CommandBody
    val main = mainCommand {
        execute<Player> { sender, _, _ ->
            sender.sendMessage("/fyk set <耐久> <名字> 设置防御块")
            sender.sendMessage("/ldq set <耐久> <范围> <标识> 设置领地旗")
            sender.sendMessage("/mmm set <耐久> <名字> 设置密码门")
            sender.sendMessage("/qzbp set <伤害> <名字> <范围> <爆破延时时间> 设置爆破炸药")
        }
    }
}