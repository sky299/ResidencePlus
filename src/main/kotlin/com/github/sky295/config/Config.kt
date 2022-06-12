package com.github.sky295.config

import com.github.sky295.ResidencePlus
import com.github.sky295.manage.FykManage
import com.github.sky295.manage.LdqManage
import com.github.sky295.manage.MMMManage
import com.github.sky295.manage.SubManage
import com.github.sky295.util.LocationUtil
import com.github.sky295.util.NBTUtil
import com.github.sky295.util.Serializable
import com.github.sky295.util.Serializable.deserializeToLocation
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

object Config {

    fun getData(key: String): String {
        if (ResidencePlus.data.getString(key)?.isNotEmpty() == true){
            return ResidencePlus.data.getString(key)!!
        }
        return "{}"
    }

    fun getFlag(block: Block):String{
        for (key in ResidencePlus.data.getKeys(false)) {
            if (LocationUtil.IsInLdqLocation(block)){
                return key
            }
        }
        return ""
    }

    fun getFykLore(itemStack: ItemStack): MutableList<String> {
        val list = mutableListOf<String>()
        for (s in ResidencePlus.config.getStringList("FYK")) {
            list.add(s.replace("%durable%", NBTUtil.getDurable(itemStack).toString()))
        }
        return list
    }

    fun getLdqLore(itemStack: ItemStack): MutableList<String> {
        val list = mutableListOf<String>()
        for (s in ResidencePlus.config.getStringList("LDQ")) {
            list.add(s.replace("%range%", NBTUtil.getRange(itemStack).toString()))
        }
        return list
    }

    fun getQzbpLore(itemStack: ItemStack): MutableList<String> {
        val list = mutableListOf<String>()
        for (s in ResidencePlus.config.getStringList("QZBP")) {
            list.add(s.replace("%power%", NBTUtil.getPower(itemStack).toString()))
        }
        return list
    }

    fun getMMMLore(itemStack: ItemStack): MutableList<String> {
        val list = mutableListOf<String>()
        for (s in ResidencePlus.config.getStringList("MMM")) {
            list.add(s.replace("%durable%", NBTUtil.getDurable(itemStack).toString()))
        }
        return list
    }

    fun hasLdqPermission(name: String, key: String): Boolean {
        if (getSubManage(key) != null) {
            for (ldqManage in getSubManage(key)!!.ldqManage) {
                if (ldqManage.permissions.contains(name)) {
                    return true
                }
            }
        }
        return false
    }

    fun hasMMMPermission(name: String, key: String): Boolean {
        if (getSubManage(key) != null){
            for (mmmManage in getSubManage(key)!!.mmmManage) {
                if (mmmManage.permissions.contains(name)){
                    return true
                }
            }
        }
        return false
    }

    fun addLdqPermission(name: String,key: String,location: Location){
        val ldq = getLdqManage(key)
        for (ldqManage in ldq) {
            if (location == ldqManage.location.deserializeToLocation()){
                val permission = ldqManage.permissions
                permission.add(name)
                ldq[ldq.indexOf(ldqManage)] = ldqManage
                addData(SubManage(getFykManage(key),ldq, getMMMManage(key)),key)
            }
        }
    }

    fun addMmmPermission(name: String,key: String,location: Location){
        val mmm = getMMMManage(key)
        for (mmmMange in mmm) {
            if (location == mmmMange.location.deserializeToLocation()){
                val permission = mmmMange.permissions
                permission.add(name)
                mmm[mmm.indexOf(mmmMange)] = mmmMange
                addData(SubManage(getFykManage(key), getLdqManage(key), mmm),key)
            }
        }
    }

    fun removeMmmPermission(name: String,key: String,location: Location){
        val mmm = getMMMManage(key)
        for (mmmMange in mmm) {
            if (location == mmmMange.location.deserializeToLocation()){
                val permission = mmmMange.permissions
                permission.remove(name)
                mmm[mmm.indexOf(mmmMange)] = mmmMange
                addData(SubManage(getFykManage(key), getLdqManage(key), mmm),key)
            }
        }
    }

    fun addData(subManage: SubManage, key: String) {
        ResidencePlus.data[key] = Serializable.serializeSub(subManage)
        ResidencePlus.data.saveToFile(ResidencePlus.data.file)
        if (ResidencePlus.data.getKeys(false).size == 1){
            for (s in ResidencePlus.data.getKeys(false)) {
                val data = getData(s)
                val manage = Serializable.deserializeSub(data)
                if (manage.ldqManage.isEmpty() && manage.fykManage.isEmpty() && manage.mmmManage.isEmpty()){
                    ResidencePlus.data[key] = null
                    ResidencePlus.data.saveToFile(ResidencePlus.data.file)
                    break
                }
            }
        }
    }

    fun getSubManage(key: String):SubManage?{
        for (s in ResidencePlus.data.getKeys(false)) {
            if (s == key){
                return Serializable.deserializeSub(getData(s))
            }
        }
        return null
    }

    fun getLdqManage(key: String): MutableList<LdqManage> {
        val list = mutableListOf<LdqManage>()
        if (getSubManage(key)?.ldqManage == null){
            return list
        }
        return Serializable.deserializeSub(getData(key)).ldqManage
    }

    fun getFykManage(key: String): MutableList<FykManage> {
        val list = mutableListOf<FykManage>()
        if (getSubManage(key)?.fykManage == null) {
            return list
        }
        return Serializable.deserializeSub(getData(key)).fykManage
    }

    fun getMMMManage(key: String): MutableList<MMMManage> {
        val list = mutableListOf<MMMManage>()
        if (getSubManage(key)?.mmmManage == null) {
            return list
        }
        return Serializable.deserializeSub(getData(key)).mmmManage
    }
}