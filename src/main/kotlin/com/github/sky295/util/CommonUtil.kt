package com.github.sky295.util

import com.github.sky295.ResidencePlus
import com.github.sky295.config.Config
import com.github.sky295.util.Serializable.deserializeToLocation
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

object CommonUtil {

    /**
     * 是否是密码门
     */
    fun isCypherDoor(block: Block): Boolean {
        if (block.isEmpty) {
            return false
        }
        if (block.type.toString().contains("DOOR")) {
            for (key in ResidencePlus.data.getKeys(false)) {
                val mmm = Config.getMMMManage(key)
                for (manage in mmm) {
                    val location = manage.location.deserializeToLocation().clone()
                    if (block.location == location || block.location == location.add(0.0, 1.0, 0.0)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    /**
     * 设置物品数量为一个
     */
    fun buildItemAmount(itemStack: ItemStack):ItemStack{
        itemStack.amount = 1
        return itemStack
    }
}