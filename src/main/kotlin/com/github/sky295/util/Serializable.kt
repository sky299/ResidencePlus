package com.github.sky295.util

import com.comphenix.protocol.utility.StreamSerializer
import com.github.sky295.manage.SubManage
import com.google.gson.GsonBuilder
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import taboolib.common.io.unzip
import taboolib.common.io.zip
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

object Serializable {

    fun serializeSub(subManage: SubManage): String {
        val gson = GsonBuilder().create()
        return gson.toJson(subManage)
    }

    fun deserializeSub(json: String): SubManage {
        val gson = GsonBuilder().create()
        return gson.fromJson(json, SubManage::class.java)
    }


    fun deserializeItemStack(data: String): ItemStack {
        val ss = StreamSerializer()
        try {
            return ss.deserializeItemStack(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ItemStack(Material.AIR)
    }

    fun serializeItemStack(item: ItemStack): String {
        val ss = StreamSerializer()
        try {
            return ss.serializeItemStack(item)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    fun ByteArray.deserializeToLocation(zipped: Boolean = true): Location {
        ByteArrayInputStream(if (zipped) unzip() else this).use { byteArrayInputStream ->
            BukkitObjectInputStream(byteArrayInputStream).use { bukkitObjectInputStream ->
                return bukkitObjectInputStream.readObject() as Location
            }
        }
    }

    fun Location.serializeToByteArray(zipped: Boolean = true): ByteArray {
        ByteArrayOutputStream().use { byteArrayOutputStream ->
            BukkitObjectOutputStream(byteArrayOutputStream).use { bukkitObjectOutputStream ->
                bukkitObjectOutputStream.writeObject(this)
                val bytes = byteArrayOutputStream.toByteArray()
                return if (zipped) bytes.zip() else bytes
            }
        }
    }
}