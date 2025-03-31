package me.weiwen.shulkerrobot.listeners

import me.weiwen.shulkerrobot.ShulkerRobot.Companion.plugin
import me.weiwen.shulkerrobot.firstShulkerBoxContaining
import me.weiwen.shulkerrobot.matches
import me.weiwen.shulkerrobot.moveFromShulkerBox
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

object RefillFromShulkerBoxListener : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!plugin.config.canRefillFromShulkerBox) return
        if (!event.canBuild()) return

        val player = event.player
        if (!player.hasPermission("shulkerrobot.refill")) return

        val item = event.itemInHand.clone()
        if (!item.matches(player.inventory.itemInMainHand, true) && !item.matches(player.inventory.itemInOffHand, true)) return

        val refillWhenAmount = (plugin.config.refillWhenRatio * item.maxStackSize).toInt()
        if (item.amount - 1 > refillWhenAmount) return

        val shulkerBoxItem = player.inventory.firstShulkerBoxContaining(item) ?: return
        val refillToAmount = (plugin.config.refillToRatio * item.maxStackSize).toInt()
        val amount = refillToAmount - event.itemInHand.amount + 1
        item.amount = amount

        val remainder = moveFromShulkerBox(shulkerBoxItem, item)
        event.itemInHand.amount += remainder.amount - 1
        if (remainder.amount != 0) {
            player.world.playSound(Sound.sound(Key.key("block.shulker_box.open"), Sound.Source.PLAYER, 0.2f, 1.0f), player)
        }
    }
}
