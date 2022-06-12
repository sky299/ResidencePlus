package com.github.sky295.listener

import com.github.sky295.ResidencePlus
import com.github.sky295.config.Config
import com.github.sky295.util.CommonUtil
import com.github.sky295.util.Serializable.deserializeToLocation
import org.bukkit.Bukkit
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import taboolib.common.platform.event.SubscribeEvent
import taboolib.library.xseries.XMaterial
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.hasLore
import taboolib.platform.util.isNotAir
import java.util.*
import kotlin.collections.HashMap

object PlayerListener {

    val map: MutableMap<UUID, String> = HashMap()

    @SubscribeEvent
    fun onInteract(event: PlayerInteractEvent) {
        if (event.hand == EquipmentSlot.HAND) {
            if (event.hasBlock() && event.item.isNotAir() && event.item!!.hasLore("强制爆破")
                && CommonUtil.isCypherDoor(event.clickedBlock!!)
                && !Config.hasMMMPermission(event.player.name,Config.getFlag(event.clickedBlock!!))){
                event.isCancelled = true
                return
            }

            //获取领地旗的放置权限
            for (key in ResidencePlus.data.getKeys(false)) {
                val ldq = Config.getLdqManage(key)
                for (manage in ldq) {
                    if (event.action == Action.RIGHT_CLICK_BLOCK
                        && event.clickedBlock?.location == manage.location.deserializeToLocation()
                        && !Config.hasLdqPermission(event.player.name,key)) {
                        Config.addLdqPermission(event.player.name,key, event.clickedBlock!!.location)
                        event.player.sendMessage("领地旗权限已获取")
                        return
                    }else if (event.player.isSneaking && event.action == Action.RIGHT_CLICK_BLOCK
                        && event.clickedBlock?.location == manage.location.deserializeToLocation()
                        && Config.hasLdqPermission(event.player.name,key)){
                        val inventory = Bukkit.createInventory(event.player, 54, "领地旗玩家权限列表")
                        for ((i,permission) in manage.permissions.withIndex()) {
                            val item = ItemBuilder(XMaterial.PLAYER_HEAD)
                            item.name = permission
                            inventory.setItem(i,item.build())
                        }
                        event.player.openInventory(inventory)
                        break
                    }
                }
            }

            //打开添加密码门权限的Gui
            for (key in ResidencePlus.data.getKeys(false)) {
                val list = Config.getMMMManage(key)
                for (manage in list) {
                    if (event.clickedBlock?.location == manage.location.deserializeToLocation()
                        && Config.hasMMMPermission(event.player.name,key) && event.hasBlock()
                        && event.player.isSneaking && event.action == Action.RIGHT_CLICK_BLOCK) {
                        val inventory = Bukkit.createInventory(event.player, 9, "密码门菜单")
                        var item = ItemBuilder(XMaterial.IRON_DOOR)
                        item.name = "§e添加白名单"
                        inventory.setItem(3, item.build())
                        item = ItemBuilder(XMaterial.PLAYER_HEAD)
                        item.name = "§e移除白名单"
                        inventory.setItem(5, item.build())
                        this.map[event.player.uniqueId] = key
                        event.player.openInventory(inventory)
                        break
                    }
                }
            }
        }
    }
}