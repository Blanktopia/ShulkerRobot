package me.weiwen.shulkerrobot.listeners

import com.destroystokyo.paper.MaterialTags
import me.weiwen.shulkerrobot.ShulkerRobot.Companion.plugin
import me.weiwen.shulkerrobot.hooks.ShulkerPacksHook.isShulkerBoxOpen
import me.weiwen.shulkerrobot.moveFromShulkerBox
import me.weiwen.shulkerrobot.moveIntoShulkerBox
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*
import org.bukkit.inventory.ItemStack

object MoveFromInventoryListener : Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onInventoryDrag(event: InventoryDragEvent) {
        if (!plugin.config.canMoveFromInventory) return
        if (event.type != DragType.SINGLE) return
        if (!MaterialTags.SHULKER_BOXES.isTagged(event.oldCursor)) return
        if (!event.view.player.hasPermission("shulkerrobot.inventory")) return
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    fun onInventoryClick(event: InventoryClickEvent) {
        if (!plugin.config.canMoveFromInventory) return
        if (event.slotType != InventoryType.SlotType.CONTAINER &&
                event.slotType != InventoryType.SlotType.QUICKBAR &&
                event.slotType != InventoryType.SlotType.CRAFTING) {
            return
        }
        if (MaterialTags.SHULKER_BOXES.isTagged(event.cursor)) {
            return onInventoryClickWithShulkerBox(event)
        }
        val item = event.currentItem ?: return
        if (MaterialTags.SHULKER_BOXES.isTagged(item)) {
            return onInventoryClickOnShulkerBox(event)
        }
    }

    fun onInventoryClickWithShulkerBox(event: InventoryClickEvent) {
        val shulkerBoxItem = event.cursor
        if (isShulkerBoxOpen(shulkerBoxItem)) return

        val item = event.currentItem ?: ItemStack.empty()

        val player = event.whoClicked
        if (!player.hasPermission("shulkerrobot.move-from-inventory")) return

        if (MaterialTags.SHULKER_BOXES.isTagged(item)) {
            event.isCancelled = true
            player.playSound(Sound.sound(Key.key("item.bundle.insert_fail"), Sound.Source.PLAYER, 1.0f, 1.0f))
            return
        }

        if (event.click == ClickType.LEFT) {
            if (item.type != Material.AIR) {
                val amount = item.amount
                val remainder = moveIntoShulkerBox(shulkerBoxItem, item)
                event.isCancelled = true
                if (amount == remainder.amount) {
                    player.playSound(Sound.sound(Key.key("item.bundle.insert_fail"), Sound.Source.PLAYER, 1.0f, 1.0f))
                    return
                }
                event.view.setItem(event.rawSlot, remainder)
                player.world.playSound(Sound.sound(Key.key("block.shulker_box.open"), Sound.Source.PLAYER, 0.2f, 1.0f), player)
            }
        } else if (event.click == ClickType.RIGHT) {
            if (item.type == Material.AIR) {
                val item = moveFromShulkerBox(shulkerBoxItem, item)
                event.isCancelled = true
                if (item.isEmpty) {
                    player.playSound(Sound.sound(Key.key("item.bundle.insert_fail"), Sound.Source.PLAYER, 1.0f, 1.0f), player)
                    return
                }
                event.view.setItem(event.rawSlot, item)
                player.world.playSound(Sound.sound(Key.key("block.shulker_box.open"), Sound.Source.PLAYER, 0.2f, 1.0f), player)
            }
        }
    }

    fun onInventoryClickOnShulkerBox(event: InventoryClickEvent) {
        val shulkerBoxItem = event.currentItem ?: return
        if (isShulkerBoxOpen(shulkerBoxItem)) return

        val item = event.cursor

        val player = event.whoClicked
        if (!player.hasPermission("shulkerrobot.move-from-inventory")) return

        if (event.click == ClickType.LEFT) {
            if (item.type != Material.AIR) {
                event.isCancelled = true
                if (MaterialTags.SHULKER_BOXES.isTagged(item)) {
                    player.playSound(Sound.sound(Key.key("item.bundle.insert_fail"), Sound.Source.PLAYER, 1.0f, 1.0f))
                    return
                }
                val amount = item.amount
                val remainder = moveIntoShulkerBox(shulkerBoxItem, item)
                if (amount == remainder.amount) {
                    player.playSound(Sound.sound(Key.key("item.bundle.insert_fail"), Sound.Source.PLAYER, 1.0f, 1.0f))
                    return
                }
                event.setCursor(remainder)
                player.world.playSound(Sound.sound(Key.key("block.shulker_box.open"), Sound.Source.PLAYER, 0.2f, 1.0f), player)
            }
        }
    }
}
