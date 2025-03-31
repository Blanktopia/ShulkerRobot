package me.weiwen.shulkerrobot.listeners

import me.weiwen.shulkerrobot.ShulkerRobot.Companion.plugin
import me.weiwen.shulkerrobot.firstShulkerBoxContaining
import me.weiwen.shulkerrobot.moveFromShulkerBox
import me.weiwen.shulkerrobot.moveIntoShulkerBox
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityPickupItemEvent

object RefillFromShulkerBoxListener : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent) {
        if (!plugin.config.canRefillFromShulkerBox) return
        if (!event.canBuild()) return

        val player = event.player
        if (!player.hasPermission("shulkerrobot.refill-from-shulker-box")) return

        val item = event.itemInHand.clone()
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
