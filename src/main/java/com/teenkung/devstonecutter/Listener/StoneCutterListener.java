package com.teenkung.devstonecutter.Listener;

import com.teenkung.devstonecutter.DevStoneCutter;
import com.teenkung.devstonecutter.Manager.CustomStonecuttingRecipe;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;


/**
 * This class handle Stonecutter's GUI and GUI Opening event
 */
public class StoneCutterListener implements Listener {

    private final DevStoneCutter plugin;

    public StoneCutterListener(DevStoneCutter plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles player clicking at stonecutter
     * @param event PlayerInteractEvent
     */
    @EventHandler
    public void onBlockInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.STONECUTTER && event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            event.getPlayer().openInventory(plugin.getGuiBuilder().buildGUI());
        }
    }

    /**
     * Give item to player, if player's inventory full it will drop in the ground instead
     * @param player target player
     * @param item item
     */
    private void giveItemToPlayer(Player player, ItemStack item) {
        HashMap<Integer, ItemStack> remainingItems = player.getInventory().addItem(item);
        if (!remainingItems.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
        }
    }

    /**
     * This is main GUI Handling method
     * @param event InventoryClickEvent
     */
    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Stone Cutter") || event.getClickedInventory() == null) {
            return;
        }

        event.setCancelled(true);

        if (event.getClickedInventory().equals(event.getWhoClicked().getInventory())) {
            handlePlayerInventoryInteract(event);
        } else {
            handleStoneCutterInventoryInteract(event);
        }
    }

    /**
     * Handles player interact with their inventory
     * @param event InventoryClickEvent (from onInventoryInteract method)
     */
    private void handlePlayerInventoryInteract(InventoryClickEvent event) {
        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) {
            return;
        }
        resetGUI(event);
        event.getWhoClicked().getOpenInventory().setItem(plugin.getGuiBuilder().getInputSlot(), event.getCurrentItem());
        Bukkit.getScheduler().runTaskLater(plugin, () -> updateSelectionSlot(event), 1);
    }

    /**
     * Handles player interact with plugin's inventory
     * @param event InventoryClickEvent (from onInventoryInteract method)
     */
    private void handleStoneCutterInventoryInteract(InventoryClickEvent event) {
        if (event.getCurrentItem() != null) {
            NBTItem nbt = new NBTItem(event.getCurrentItem());
            if (!nbt.getBoolean("isBackground")) {
                if (plugin.getGuiBuilder().getSelectionSlot().contains(event.getSlot())) {
                    event.getWhoClicked().getOpenInventory().setItem(plugin.getGuiBuilder().getOutputSlot(), event.getCurrentItem());
                } else if (event.getSlot() == plugin.getGuiBuilder().getOutputSlot()) {
                    handleOutputSlotInteract(event);
                } else if (event.getSlot() == plugin.getGuiBuilder().getInputSlot()) {
                    resetGUI(event);
                }
            }
        }
    }

    /**
     * Handles player click at output slot of the plugin's GUI
     * @param event InventoryClickEvent (from handleStoneCutterInventoryInteract method)
     */
    private void handleOutputSlotInteract(InventoryClickEvent event) {
        if ((event.getClickedInventory() != null) && (event.getCurrentItem() == plugin.getGuiBuilder().getItem(String.valueOf(plugin.getGuiBuilder().getOutputChar())))) {
            return;
        }

        ItemStack give = event.getClickedInventory().getItem(plugin.getGuiBuilder().getOutputSlot());
        ItemStack input = event.getClickedInventory().getItem(plugin.getGuiBuilder().getInputSlot());

        if (give == null || input == null) {
            return;
        }

        if (event.getClick().equals(ClickType.LEFT)) {
            removeFromPlayer((Player) event.getWhoClicked(), input, 1);
            input.setAmount(input.getAmount() - 1);

            if (input.getAmount() == 0) {
                resetGUI(event);
            } else {
                event.getClickedInventory().setItem(plugin.getGuiBuilder().getInputSlot(), input);
            }
            giveItemToPlayer((Player) event.getWhoClicked(), give);
        } else if (event.getClick().equals(ClickType.SHIFT_LEFT)) {
            give.setAmount(give.getAmount() * input.getAmount());
            removeFromPlayer((Player) event.getWhoClicked(), input, input.getAmount());
            resetGUI(event);
            giveItemToPlayer((Player) event.getWhoClicked(), give);
        }
    }

    /**
     * reset the plugin's GUI
     * @param event InventoryClickEvent
     */
    private void resetGUI(InventoryClickEvent event) {
        event.getWhoClicked().getOpenInventory().setItem(plugin.getGuiBuilder().getInputSlot(), plugin.getGuiBuilder().getItem(String.valueOf(plugin.getGuiBuilder().getInputChar())));
        event.getWhoClicked().getOpenInventory().setItem(plugin.getGuiBuilder().getOutputSlot(), plugin.getGuiBuilder().getItem(String.valueOf(plugin.getGuiBuilder().getOutputChar())));

        for (int i : plugin.getGuiBuilder().getSelectionSlot()) {
            event.getWhoClicked().getOpenInventory().setItem(i, plugin.getGuiBuilder().getItem(String.valueOf(plugin.getGuiBuilder().getSelectionChar())));
        }
    }

    /**
     * take an Item from player's inventory
     * @param player target player
     * @param stack itemStack you want to take from player
     * @param amount amount of itemStack you want to take
     */
    public void removeFromPlayer(Player player, ItemStack stack, int amount) {
        ItemStack[] contents = player.getInventory().getContents();
        int remaining = amount;

        for (int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if (item != null && item.isSimilar(stack)) {
                if (item.getAmount() > remaining) {
                    item.setAmount(item.getAmount() - remaining);
                    return;
                } else {
                    remaining -= item.getAmount();
                    player.getInventory().setItem(i, null);
                }
            }

            if (remaining <= 0) {
                break;
            }
        }
    }

    /**
     * Update the plugin's GUI to match the recipe
     * @param event InventoryClickEvent
     */
    private void updateSelectionSlot(InventoryClickEvent event) {
        CustomStonecuttingRecipe recipe = plugin.getRecipeManager().getStoneCutterRecipes().getOrDefault(plugin.turnToString(event.getCurrentItem()), null);
        if (recipe != null) {
            int i = 0;
            for (int slot : plugin.getGuiBuilder().getSelectionSlot()) {
                if (i < recipe.getResult().size()) {
                    String result = recipe.getResult().get(i);
                    int resultAmount = recipe.getResultAmount(result);
                    event.getWhoClicked().getOpenInventory().setItem(slot, plugin.getItemFromString(result, resultAmount));
                } else {
                    event.getWhoClicked().getOpenInventory().setItem(slot, plugin.getGuiBuilder().getItem(String.valueOf(plugin.getGuiBuilder().getSelectionChar())));
                }
                i++;
            }
        }
    }

}
