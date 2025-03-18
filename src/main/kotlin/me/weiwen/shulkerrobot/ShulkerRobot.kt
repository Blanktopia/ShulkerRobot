package me.weiwen.shulkerrobot

import me.weiwen.shulkerrobot.config.Config
import me.weiwen.shulkerrobot.config.parseConfig
import me.weiwen.shulkerrobot.listeners.MoveFromInventoryListener
import me.weiwen.shulkerrobot.listeners.PickIntoShulkerBoxListener
import me.weiwen.shulkerrobot.listeners.RefillFromShulkerBoxListener
import org.bstats.bukkit.Metrics
import org.bukkit.plugin.java.JavaPlugin
import java.net.http.WebSocket

class ShulkerRobot : JavaPlugin(), WebSocket.Listener {
    companion object {
        lateinit var plugin: ShulkerRobot
            private set
        lateinit var metrics: Metrics
            private set
    }

    lateinit var config: Config

    override fun onLoad() {
        plugin = this
    }

    override fun onEnable() {
        config = parseConfig(this)
        metrics = Metrics(this, 15850)

        server.pluginManager.registerEvents(MoveFromInventoryListener, this)
        server.pluginManager.registerEvents(PickIntoShulkerBoxListener, this)
        server.pluginManager.registerEvents(RefillFromShulkerBoxListener, this)

        logger.info("ShulkerRobot is enabled")
    }

    override fun onDisable() {
        logger.info("ShulkerRobot is disabled")
    }

}
