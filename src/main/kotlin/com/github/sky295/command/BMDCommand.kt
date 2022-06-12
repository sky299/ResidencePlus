package com.github.sky295.command

import com.github.sky295.ResidencePlus
import com.github.sky295.config.Config
import com.github.sky295.listener.PlayerListener
import com.github.sky295.util.Serializable.deserializeToLocation
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers

@CommandHeader("bmd")
object BMDCommand {

    @CommandBody
    val set = subCommand {
        dynamic(optional = true) {
            suggestion<Player> { _, _ ->
                onlinePlayers().map { it.name }
            }
            execute<Player> { sender, _, argument ->
                for (key in ResidencePlus.data.getKeys(false)) {
                    for (s in Config.getMMMManage(key)) {
                        if (PlayerListener.map.containsKey(sender.uniqueId)){
                            Config.addMmmPermission(argument,key,s.location.deserializeToLocation())
                            PlayerListener.map.remove(sender.uniqueId)
                            sender.sendMessage("密码门权限添加成功")
                            break
                        }else{
                            sender.sendMessage("该玩家不存在")
                            break
                        }
                    }
                }
            }
        }
    }
}