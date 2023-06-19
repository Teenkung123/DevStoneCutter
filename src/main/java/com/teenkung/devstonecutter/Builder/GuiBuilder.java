package com.teenkung.devstonecutter.Builder;

import com.teenkung.devstonecutter.DevStoneCutter;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;

public class GuiBuilder {
    private final DevStoneCutter plugin;
    private List<String> layout;
    private Map<String, ItemStack> items;
    private char inputChar;
    private char outputChar;
    private char selectChar;
    private int inputSlot;
    private int outputSlot;
    private List<Integer> selectSlot;

    public GuiBuilder(DevStoneCutter plugin) {
        this.plugin = plugin;
    }

    public void loadGUI() {
        try {
            loadConfig();
            Bukkit.getLogger().info(ChatColor.GREEN + "GUI loaded successfully.");
        } catch (Exception e) {
            Bukkit.getLogger().severe(ChatColor.RED + "Error loading GUI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadConfig() throws Exception {
        FileConfiguration config = plugin.getConfig();
        layout = config.getStringList("GUI.Layout");
        if (layout.isEmpty()) {
            throw new Exception("GUI.Layout is missing or empty in the configuration.");
        }

        ConfigurationSection itemsSection = config.getConfigurationSection("GUI.Items");
        if (itemsSection == null) {
            throw new Exception("GUI.Items is missing in the configuration.");
        }

        items = new HashMap<>();
        for (String key : itemsSection.getKeys(false)) {
            String materialName = itemsSection.getString(key + ".Material");
            if (materialName == null) {
                throw new Exception("Invalid material name");
            }
            Material material = Material.getMaterial(materialName.toUpperCase());
            if (material == null) {
                throw new Exception("Invalid material name: " + materialName);
            }

            ItemStack item = new ItemStack(material);
            items.put(key, item);
        }

        selectSlot = new ArrayList<>();
        inputChar = config.getString("GUI.InputSlot", "E").charAt(0);
        outputChar = config.getString("GUI.OutputSlot", "R").charAt(0);
        selectChar = config.getString("GUI.SelectSlot", "A").charAt(0);
        int rows = layout.size();
        for (int i = 0; i < rows; i++) {
            String row = layout.get(i);
            for (int j = 0; j < 9; j++) {
                char c = row.charAt(j);

                if (c == outputChar) {
                    outputSlot = i * 9 + j;
                }
                if (c == inputChar) {
                    inputSlot = i * 9 + j;
                }
                if (c == selectChar) {
                    selectSlot.add(i * 9 + j);
                }
            }
        }
    }

    public Inventory buildGUI() {
        int rows = layout.size();
        Inventory gui = Bukkit.createInventory(null, rows * 9, "Stone Cutter");

        for (int i = 0; i < rows; i++) {
            String row = layout.get(i);
            for (int j = 0; j < 9; j++) {
                char c = row.charAt(j);
                String itemId = String.valueOf(c);
                ItemStack item = items.get(itemId);
                if (item != null) {
                    if (item.getType() != Material.AIR) {
                        NBTItem nbt = new NBTItem(item);
                        nbt.setBoolean("isBackground", true);
                        nbt.applyNBT(item);
                    }
                    gui.setItem(i * 9 + j, item);
                }
            }
        }

        return gui;
    }

    public int getInputSlot() { return inputSlot; }
    public int getOutputSlot() { return outputSlot; }
    public List<Integer> getSelectionSlot() { return selectSlot; }

    public char getInputChar() { return inputChar; }
    public char getOutputChar() { return outputChar; }
    public char getSelectionChar() { return selectChar; }

    public ItemStack getItem(String itemId) { return items.get(itemId); }



}
