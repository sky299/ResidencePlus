package com.github.sky295.listener

import com.github.sky295.ResidencePlus
import com.github.sky295.config.Config
import com.github.sky295.util.Serializable.deserializeToLocation
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryClickEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.isNotAir

object UIListener {

    @SubscribeEvent
    fun onClick(event: InventoryClickEvent) {
        if (event.view.title == "密码门菜单") {
            event.isCancelled = true
            if (event.rawSlot == 3) {
                event.whoClicked.sendMessage("请输入/bmd set 玩家id 添加玩家到密码门的白名单中")
            } else if (event.rawSlot == 5) {
                val inv = Bukkit.createInventory(event.whoClicked, 54, "管理白名单")
                for (key in ResidencePlus.data.getKeys(false)) {
                    if (key == PlayerListener.map[event.whoClicked.uniqueId]) {
                        val mmm = Config.getMMMManage(key)
                        for (mmmManage in mmm) {
                            for ((i, permission) in mmmManage.permissions.withIndex()) {
                                val item = ItemBuilder(XMaterial.PLAYER_HEAD)
                                item.name = permission
                                item.lore += "§e点击移出白名单"
                                inv.setItem(i, item.build())
                            }
                            event.whoClicked.openInventory(inv)
                            break
                        }
                        break
                    }
                }
            }
        } else if (event.view.title == "管理白名单") {
            event.isCancelled = true
            for (key in ResidencePlus.data.getKeys(false)) {
                for (s in Config.getMMMManage(key)) {
                    if (event.currentItem.isNotAir() && key == PlayerListener.map[event.whoClicked.uniqueId]) {
                        Config.removeMmmPermission(event.whoClicked.name,key,s.location.deserializeToLocation())
                        PlayerListener.map.remove(event.whoClicked.uniqueId)
                        event.currentItem = ItemBuilder(XMaterial.AIR).build()
                        event.whoClicked.sendMessage("权限移除成功")
                        break
                    } else {
                        event.whoClicked.sendMessage("该玩家不存在")
                        break
                    }
                }
            }
        }else if (event.view.title == "领地旗玩家权限列表"){
            event.isCancelled = true
        }
    }
}