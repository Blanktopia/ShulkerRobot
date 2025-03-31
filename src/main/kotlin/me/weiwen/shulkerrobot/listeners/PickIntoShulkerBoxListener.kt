package me.weiwen.shulkerrobot.listeners

import com.destroystokyo.paper.MaterialTags
import me.weiwen.shulkerrobot.ShulkerRobot.Companion.plugin
import me.weiwen.shulkerrobot.firstShulkerBoxContaining
import me.weiwen.shulkerrobot.hooks.ShulkerPacksHook.isShulkerBoxOpen
import me.weiwen.shulkerrobot.matches
import me.weiwen.shulkerrobot.moveIntoShulkerBox
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.block.ShulkerBox
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.inventory.meta.BlockStateMeta

object PickIntoShulkerBoxListener : Listener {
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onPickupItem(event: EntityPickupItemEvent) {
        if (!plugin.config.canPickIntoShulkerBox) return

        val player = event.entity as? Player ?: return
        if (!player.hasPermission("shulkerrobot.pick")) return

        val itemStack = event.item.itemStack
        val amount = itemStack.amount
        var remainder = itemStack

        if (player.inventory.containsAtLeast(itemStack, 1)) {
            return
        }

        for (item in player.inventory.filterNotNull()) {
            if (!MaterialTags.SHULKER_BOXES.isTagged(item)) continue

            val blockStateMeta = item.itemMeta as? BlockStateMeta ?: continue
            val shulkerBox = blockStateMeta.blockState as? ShulkerBox ?: continue
            if (isShulkerBoxOpen(item)) continue

            if (!shulkerBox.inventory.filterNotNull().any { it.matches(remainder, true) }) {
                continue
            }

            remainder = moveIntoShulkerBox(item, remainder)
            if (remainder.amount == 0) {
                break
            }
        }

        event.item.itemStack = itemStack
        if (amount - remainder.amount != 0) {
            player.world.playSound(Sound.sound(Key.key("block.shulker_box.open"), Sound.Source.PLAYER, 0.2f, 1.0f), player)
        }
    }
}
