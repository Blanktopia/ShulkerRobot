package me.weiwen.shulkerrobot.listeners

import me.weiwen.shulkerrobot.ShulkerRobot.Companion.plugin
import me.weiwen.shulkerrobot.firstShulkerBoxContaining
import me.weiwen.shulkerrobot.moveIntoShulkerBox
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent

object PickIntoShulkerBoxListener : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPickupItem(event: EntityPickupItemEvent) {
        if (!plugin.config.canPickIntoShulkerBox) return

        val player = event.entity as? Player ?: return
        if (!player.hasPermission("shulkerrobot.pick")) return

        val item = event.item.itemStack
        val amount = item.amount
        val shulkerBoxItem = player.inventory.firstShulkerBoxContaining(item) ?: return

        val remainder = moveIntoShulkerBox(shulkerBoxItem, item)
        item.amount = remainder.amount
        event.item.itemStack = item
        if (amount - remainder.amount != 0) {
            player.world.playSound(Sound.sound(Key.key("block.shulker_box.open"), Sound.Source.PLAYER, 0.2f, 1.0f), player)
        }
    }
}
