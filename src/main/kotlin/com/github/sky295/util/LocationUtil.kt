package com.github.sky295.util

import com.github.sky295.ResidencePlus
import com.github.sky295.config.Config
import com.github.sky295.util.Serializable.deserializeToLocation
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.util.BoundingBox
import org.bukkit.util.Vector

object LocationUtil {

    fun overlaps(a: Location, b: Location, rangeA: Double, rangeB: Double): Boolean {
        return overlaps(
            getFirstLocation(a, rangeA), getSecondLocation(a, rangeA),
            getFirstLocation(b, rangeB), getSecondLocation(b, rangeB)
        )
    }

    /**
     * 参数传入两矩形对角坐标
     */
    private fun overlaps(a: Location, a1: Location, b: Location, b1: Location): Boolean {
        return BoundingBox(a.x, a.y, a.z, a1.x, a1.y, a1.z).overlaps(BoundingBox(b.x, b.y, b.z, b1.x, b1.y, b1.z))
    }

    fun isInLocation(block: Block, location: Location, range: Double): Boolean {
        if (block.isEmpty) {
            return true
        }
        val loc = getFirstLocation(location, range)
        val loc1 = getSecondLocation(location, range)
        val x = block.x
        val y = block.y
        val z = block.z
        return x >= Math.min(loc.blockX, loc1.blockX) && x <= Math.max(loc.blockX, loc1.blockX)
                && y >= Math.min(loc.blockY, loc1.blockY) && y <= Math.max(loc.blockY, loc1.blockY)
                && z >= Math.min(loc.blockZ, loc1.blockZ) && z <= Math.max(loc.blockZ, loc1.blockZ)
    }

    private fun getFirstLocation(loc: Location, range: Double): Location {
        val x = loc.blockX - range
        val y = loc.blockY - range
        val z = loc.blockZ - range
        return Location(loc.world, x, y, z)
    }

    private fun getSecondLocation(loc: Location, range: Double): Location {
        val x = loc.blockX + range
        val y = loc.blockY + range
        val z = loc.blockZ + range
        return Location(loc.world, x, y, z)
    }

    fun IsInLdqLocation(block: Block): Boolean {
        for (key in ResidencePlus.data.getKeys(false)) {
            for (ldqManage in Serializable.deserializeSub(Config.getData(key)).ldqManage) {
                if (isInLocation(block, ldqManage.location.deserializeToLocation(),
                        NBTUtil.getRange(Serializable.deserializeItemStack(ldqManage.itemStack)))){
                    return true
                }
            }
        }
        return false
    }

    /**
     * 获取方块范围内的所有方块
     */
    fun getRegionBlocks(block: Block,range: Double):MutableList<Block>{
        val blocks = mutableListOf<Block>()

        val loc = getFirstLocation(block.location, range)
        val loc1 = getSecondLocation(block.location, range)

        val x1 = loc.blockX
        val y1 = loc.blockY
        val z1 = loc.blockZ

        val x2 = loc1.blockX
        val y2 = loc1.blockY
        val z2 = loc1.blockZ

        for (x in Math.min(x1,x2)..Math.max(x1,x2)){
            for (y in Math.min(y1,y2)..Math.max(y1,y2)){
                for (z in Math.min(z1,z2)..Math.max(z1,z2)){
                    blocks.add(loc.world!!.getBlockAt(x,y,z))
                }
            }
        }
        return blocks
    }

    fun getMultiplyBlocks(block: Block,vec :Vector,range: Double):MutableList<Block>{
        return getRegionBlocks(block.location.clone().add(vec.multiply(range)).block,range)
    }
}