package com.github.sky295.listener

import com.github.sky295.ResidencePlus
import com.github.sky295.config.Config
import com.github.sky295.manage.FykManage
import com.github.sky295.manage.LdqManage
import com.github.sky295.manage.MMMManage
import com.github.sky295.manage.SubManage
import com.github.sky295.util.CommonUtil
import com.github.sky295.util.LocationUtil
import com.github.sky295.util.NBTUtil
import com.github.sky295.util.Serializable
import com.github.sky295.util.Serializable.deserializeToLocation
import com.github.sky295.util.Serializable.serializeToByteArray
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.hanging.HangingPlaceEvent
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.platform.util.hasLore

object BlockListener {

    @SubscribeEvent
    fun onPlace(event: BlockPlaceEvent) {
        val block = event.block
        val player = event.player
        for (key in ResidencePlus.data.getKeys(false)) {
            if (LocationUtil.IsInLdqLocation(block) && !Config.hasLdqPermission(player.name, key)
                && !event.itemInHand.hasLore("强制爆破")) {
                player.sendMessage("你没有权限在该领地内放置方块")
                event.isCancelled = true
            }
        }
        if (event.itemInHand.hasLore("领地旗")) {
            if (ResidencePlus.data.getKeys(false).isEmpty()) {
                val flag = NBTUtil.getFlag(event.itemInHand)
                val ldq = Config.getLdqManage(flag)
                ldq.add(LdqManage(block.location.serializeToByteArray(),
                    Serializable.serializeItemStack(event.itemInHand), mutableListOf(player.name)))
                Config.addData(SubManage(Config.getFykManage(flag), ldq, Config.getMMMManage(flag)), flag)
            } else {
                val flag = NBTUtil.getFlag(event.itemInHand)
                val data = Config.getData(flag)
                for (ldqManage in Serializable.deserializeSub(data).ldqManage) {
                    if (LocationUtil.overlaps(block.location, ldqManage.location.deserializeToLocation(),
                            NBTUtil.getRange(event.itemInHand),
                            NBTUtil.getRange(Serializable.deserializeItemStack(ldqManage.itemStack)))) {
                        player.sendMessage("你不能把领地旗放置在其他领地旗范围内")
                        event.isCancelled = true
                    } else {
                        val permissions = ldqManage.permissions
                        val ldq = Config.getLdqManage(flag)
                        if (!permissions.contains(player.name)) {
                            permissions.add(player.name)
                        }
                        ldq.add(LdqManage(block.location.serializeToByteArray(),
                            Serializable.serializeItemStack(event.itemInHand), permissions))
                        Config.addData(SubManage(Config.getFykManage(flag), ldq, Config.getMMMManage(flag)), flag)
                    }
                }
            }
        } else if (event.itemInHand.hasLore("防御块")) {
            if (!LocationUtil.IsInLdqLocation(block)) {
                player.sendMessage("防御块需放置在领地范围内")
                event.isCancelled = true
            } else {
                for (key in ResidencePlus.data.getKeys(false)) {
                    if (LocationUtil.IsInLdqLocation(block)) {
                        val fyk = Config.getFykManage(key)
                        fyk.add(FykManage(block.location.serializeToByteArray(),
                            Serializable.serializeItemStack(event.itemInHand)))
                        Config.addData(SubManage(fyk, Config.getLdqManage(key), Config.getMMMManage(key)), key)
                        break
                    }
                }
            }
        } else if (event.itemInHand.hasLore("密码门")) {
            for (key in ResidencePlus.data.getKeys(false)) {
                if (LocationUtil.IsInLdqLocation(block)) {
                    val mmm = Config.getMMMManage(key)
                    var permissions = mutableListOf<String>()
                    if (mmm.isEmpty()) {
                        permissions.add(player.name)
                    } else {
                        for (mmmManage in mmm) {
                            permissions = mmmManage.permissions
                            if (!permissions.contains(player.name)) {
                                permissions.add(player.name)
                                break
                            }
                        }
                    }
                    mmm.add(MMMManage(block.location.serializeToByteArray(),
                        Serializable.serializeItemStack(event.itemInHand), permissions))
                    Config.addData(SubManage(Config.getFykManage(key), Config.getLdqManage(key), mmm),
                        Config.getFlag(block))
                    break
                }
            }
        }
    }

    @SubscribeEvent
    fun onBreak(event: BlockBreakEvent) {
        //用建筑锤破坏防御块的时候，从配置文件中删除它
        for (key in ResidencePlus.data.getKeys(false)) {
            val fyk = Config.getFykManage(key)
            for (fykManage in fyk) {
                if (fykManage.location.deserializeToLocation() == event.block.location) {
                    if (LocationUtil.IsInLdqLocation(event.block)
                        && !Config.hasLdqPermission(event.player.name, key)) {
                        event.player.sendMessage("你没有权限破坏防御块")
                        event.isCancelled = true
                        break
                    } else if (Config.hasLdqPermission(event.player.name, key)
                        && !event.player.inventory.itemInMainHand.hasLore("建筑锤")) {
                        event.player.sendMessage("你不能使用非建筑锤破坏防御块")
                        event.isCancelled = true
                        break
                    } else {
                        event.isDropItems = false
                        event.player.inventory.addItem(CommonUtil.buildItemAmount(Serializable.deserializeItemStack(fykManage.itemStack)))
                        fyk.remove(fykManage)
                        Config.addData(SubManage(fyk, Config.getLdqManage(key), Config.getMMMManage(key)), key)
                        break
                    }
                }
            }
        }

        //用建筑锤破坏领地旗的时候，从配置文件中删除它
        for (key in ResidencePlus.data.getKeys(false)) {
            val ldq = Config.getLdqManage(key)
            for (manage in ldq) {
                if (event.block.location == manage.location.deserializeToLocation()) {
                    if (!Config.hasLdqPermission(event.player.name, key)
                        || Config.hasLdqPermission(event.player.name, key)
                        && !event.player.inventory.itemInMainHand.hasLore("建筑锤")) {
                        event.player.sendMessage("你无法破坏该领地旗")
                        event.isCancelled = true
                        break
                    } else {
                        event.isCancelled = true
                        event.player.inventory.addItem(CommonUtil.buildItemAmount(Serializable.deserializeItemStack(manage.itemStack)))
                        ldq.remove(manage)
                        Config.addData(SubManage(Config.getFykManage(key), ldq, Config.getMMMManage(key)), key)
                        break
                    }
                }
            }
        }

        //如果是密码门并且不是用建筑锤破坏的就取消它
        for (key in ResidencePlus.data.getKeys(false)) {
            val mmm = Config.getMMMManage(key)
            for (manage in mmm) {
                if (CommonUtil.isCypherDoor(event.block)) {
                    if (LocationUtil.IsInLdqLocation(event.block) && !Config.hasMMMPermission(event.player.name, key)) {
                        event.isCancelled = true
                        event.player.sendMessage("你没有权限破坏密码门")
                        break
                    } else if (Config.hasMMMPermission(event.player.name, key)
                        && !event.player.inventory.itemInMainHand.hasLore("建筑锤")) {
                        event.isCancelled = true
                        event.player.sendMessage("你不能使用非建筑锤破坏密码门")
                        break
                    } else {
                        event.isCancelled = true
                        event.player.inventory.addItem(CommonUtil.buildItemAmount(Serializable.deserializeItemStack(manage.itemStack)))
                        mmm.remove(manage)
                        Config.addData(SubManage(Config.getFykManage(key), Config.getLdqManage(key), mmm), key)
                        break
                    }
                }
            }
        }
    }

    @SubscribeEvent
    fun onHangingPlace(event: HangingPlaceEvent) {
        val itemStack = event.player!!.inventory.itemInMainHand

        if (itemStack.hasLore("强制爆破")) {
            val blocks = LocationUtil.getMultiplyBlocks(event.block,event.blockFace.direction,NBTUtil.getRange(itemStack))
            repeat(NBTUtil.getTime(itemStack)){
                event.player!!.playSound(event.block.location, Sound.ENTITY_PLAYER_HURT_ON_FIRE,1F,10F)
            }
            submit(delay = NBTUtil.getTime(itemStack) * 20L) {
                for (key in ResidencePlus.data.getKeys(false)) {
                    val fyk = Config.getFykManage(key)
                    val mmm = Config.getMMMManage(key)
                    val f = fyk.iterator()
                    val m = mmm.iterator()
                    while (f.hasNext()){
                        val fykManage = f.next()
                        for (b in blocks) {
                            if (fykManage.location.deserializeToLocation() == b.location) {
                                f.remove()
                                b.type = Material.AIR
                            }
                        }
                    }
                    while (m.hasNext()){
                        val mmmManage = m.next()
                        for (b in blocks) {
                            if (mmmManage.location.deserializeToLocation() == b.location) {
                                mmm.remove(mmmManage)
                                b.type = Material.AIR
                            }
                        }
                    }
                    event.entity.remove()
                    Config.addData(SubManage(fyk, Config.getLdqManage(key), mmm), key)
                }
                this.cancel()
            }
        }
    }
}