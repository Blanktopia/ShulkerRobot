package me.weiwen.shulkerrobot.config

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.*
import me.weiwen.shulkerrobot.ShulkerRobot
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.logging.Level

const val CONFIG_VERSION = "1.0.0"

@Serializable
data class Config(
    @SerialName("config-version")
    var configVersion: String = "1.0.0",

    @SerialName("can-pick-into-shulker-box")
    var canPickIntoShulkerBox: Boolean = true,

    @SerialName("can-refill-from-shulker-box")
    var canRefillFromShulkerBox: Boolean = true,

    @SerialName("refill-when-ratio")
    var refillWhenRatio: Double = 0.0,

    @SerialName("refill-to-ratio")
    var refillToRatio: Double = 1.0,

    @SerialName("can-move-from-inventory")
    var canMoveFromInventory: Boolean = true,
)

fun parseConfig(plugin: ShulkerRobot): Config {
    val file = File(plugin.dataFolder, "config.yml")

    if (!file.exists()) {
        plugin.logger.log(Level.INFO, "Config file not found, creating default")
        plugin.dataFolder.mkdirs()
        file.createNewFile()
        file.writeText(Yaml().encodeToString(Config()))
    }

    val config = try {
        Yaml().decodeFromString(file.readText())
    } catch (e: Exception) {
        plugin.logger.log(Level.SEVERE, e.message)
        Config()
    }

    if (config.configVersion != CONFIG_VERSION) {
        config.configVersion = CONFIG_VERSION
        plugin.logger.log(Level.INFO, "Updating config")
        file.writeText(Yaml().encodeToString(plugin.config))
    }

    return config
}
