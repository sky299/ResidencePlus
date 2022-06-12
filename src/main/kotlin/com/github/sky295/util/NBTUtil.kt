package com.github.sky295.util

import org.bukkit.inventory.ItemStack
import taboolib.module.nms.getItemTag
import taboolib.module.nms.setItemTag

object NBTUtil {

    fun setTime(itemStack: ItemStack, time: Int): ItemStack {
        val itemTag = itemStack.getItemTag()
        itemTag.putDeep("time", time)
        return itemStack.setItemTag(itemTag)
    }

    fun getTime(itemStack: ItemStack): Int {
        val itemTag = itemStack.getItemTag()
        if (itemTag.getDeep("time") != null) {
            return itemTag.getDeep("time").asInt()
        }
        return 0
    }


    fun setFlag(itemStack: ItemStack, flag: String): ItemStack {
        val itemTag = itemStack.getItemTag()
        itemTag.putDeep("flag", flag)
        return itemStack.setItemTag(itemTag)
    }

    fun getFlag(itemStack: ItemStack): String {
        val itemTag = itemStack.getItemTag()
        if (itemTag.getDeep("flag") != null) {
            return itemTag.getDeep("flag").asString()
        }
        return ""
    }

    fun setPower(itemStack: ItemStack, power: Int): ItemStack {
        val itemTag = itemStack.getItemTag()
        itemTag.putDeep("power", power)
        return itemStack.setItemTag(itemTag)
    }

    fun getPower(itemStack: ItemStack): Int {
        val itemTag = itemStack.getItemTag()
        if (itemTag.getDeep("power") != null) {
            return itemTag.getDeep("power").asInt()
        }
        return 0
    }


    fun setRange(itemStack: ItemStack, range: Int): ItemStack {
        val itemTag = itemStack.getItemTag()
        itemTag.putDeep("range", range)
        return itemStack.setItemTag(itemTag)
    }

    fun getRange(itemStack: ItemStack): Double {
        val itemTag = itemStack.getItemTag()
        if (itemTag.getDeep("range") != null) {
            return itemTag.getDeep("range").asDouble()
        }
        return 0.0
    }

    fun setDurable(itemStack: ItemStack, durable: Int): ItemStack {
        val itemTag = itemStack.getItemTag()
        itemTag.putDeep("durable", durable)
        return itemStack.setItemTag(itemTag)
    }

    fun getDurable(itemStack: ItemStack): Int {
        val itemTag = itemStack.getItemTag()
        if (itemTag.getDeep("durable") != null) {
            return itemTag.getDeep("durable").asInt()
        }
        return 0
    }
}