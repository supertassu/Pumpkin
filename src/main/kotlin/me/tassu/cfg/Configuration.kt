package me.tassu.cfg

import com.typesafe.config.Config
import com.typesafe.config.ConfigException
import me.tassu.util.containsMethod
import me.tassu.util.doesNotThrow
import me.tassu.util.replaceColors
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

@Suppress("MemberVisibilityCanBePrivate")
open class Configuration(private val config: Config) {

    companion object {
        private val CONFIG_EXCEPTION: Class<out Throwable> = ConfigException::class.java
    }

    /// ===== MEMBER FUNCTIONS =====

    // list keys

    fun getKeys(path: String = ""): Iterable<String> {
        var config = this.config

        if (path.isNotBlank()) {
            config = this.config.getConfig(path)
        }

        return config.entrySet().map { it.key }.toSet()
    }

    // contains

    fun contains(id: String): Boolean {
        return !config.hasPathOrNull(id)
    }

    // strings

    fun isString(id: String): Boolean {
        return {
            config.getString(id)
        }.doesNotThrow(CONFIG_EXCEPTION)
    }

    fun getString(id: String, color: Boolean = true): String {
        var string = config.getString(id)
        if (color) string = string.replaceColors()
        return string
    }

    // string lists

    fun isStringList(id: String): Boolean {
        return {
            config.getString(id)
        }.doesNotThrow(CONFIG_EXCEPTION)
    }

    fun getStringList(id: String, color: Boolean = true): List<String> {
        var list =  config.getStringList(id)
        if (color) list = list.map { it.replaceColors() }
        return list
    }

    // ints

    fun isInt(id: String): Boolean {
        return {
            config.getInt(id)
        }.doesNotThrow(CONFIG_EXCEPTION)
    }

    fun getInt(id: String): Int {
        return config.getInt(id)
    }

    // booleans

    fun isBoolean(id: String): Boolean {
        return {
            config.getBoolean(id)
        }.doesNotThrow(CONFIG_EXCEPTION)
    }

    fun getBoolean(id: String): Boolean {
        return config.getBoolean(id)
    }

    // BUKKIT OBJECTS

    // material

    fun isMaterial(id: String): Boolean {
        return {
            Material.valueOf(getString(id, color = true))
        }.doesNotThrow(CONFIG_EXCEPTION, IllegalArgumentException::class.java)
    }

    fun getMaterial(id: String): Material {
        return Material.valueOf(getString(id, color = false))
    }

    // ADVANCED OBJECTS

    // sections

    fun isSection(id: String): Boolean {
        return {
            config.getConfig(id)
        }.doesNotThrow()
    }

    fun getConfig(id: String): Configuration {
        return Configuration(config.getConfig(id))
    }

    // item stacks

    fun isItemStack(id: String): Boolean {
        if (!contains(id)) return false
        if (!isSection(id)) return false

        if (!contains("$id.material")) return false
        return isMaterial("$id.material")
    }

    fun getItemStack(id: String): ItemStack {
        if (!isItemStack(id)) throw IllegalArgumentException("not an itemstack: $id")
        val itemCfg = getConfig(id)

        val item = ItemStack(itemCfg.getMaterial("material"))

        if (itemCfg.contains("amount")) {
            item.amount = itemCfg.getInt("amount")
        }

        if (itemCfg.contains("durability") && item.containsMethod("setDurability")) {
            item.durability = itemCfg.getInt("data").toShort()
        }

        val meta = item.itemMeta

        if (itemCfg.contains("name")) {
            meta.displayName = itemCfg.getString("name")
        } else if (itemCfg.contains("i18n")) {
            meta.localizedName = itemCfg.getString("i18n")
        }

        if (itemCfg.contains("lore")) {
            meta.lore = itemCfg.getStringList("lore")
        }

        if (itemCfg.contains("flags")) {
            itemCfg.getStringList("flags").stream()
                    .map { ItemFlag.valueOf(it) }
                    .forEach { meta.addItemFlags(it) }
        }

        if (itemCfg.contains("enchantments")) {
            itemCfg.getStringList("enchantments").stream()
                    .map {
                        val split = it.split(" ")
                        Pair(split[0], if (split.size == 1) 1 else split[1].toInt())
                    }
                    .map({ string: String, int: Int -> Pair(string, int) })
                    .forEach { meta.addItemFlags(it) }
        }

        item.itemMeta = meta
        return item
    }

}