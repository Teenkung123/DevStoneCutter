package com.teenkung.devstonecutter;

import com.teenkung.devstonecutter.Manager.CustomStonecuttingRecipe;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;

public class ConfigLoader {

    private final DevStoneCutter plugin;
    private HashMap<String, CustomStonecuttingRecipe> recipes;

    public ConfigLoader(DevStoneCutter plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.getConfig().options().copyDefaults(true);
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        loadRecipesFromConfig();
    }

    public HashMap<String, CustomStonecuttingRecipe> getRecipes() {
        return recipes;
    }

    private void loadRecipesFromConfig() {
        this.recipes = new HashMap<>();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection recipesSection = config.getConfigurationSection("Recipes");
        if (recipesSection != null) {
            int rcount = 0;
            for (String key : recipesSection.getKeys(false)) {
                try {
                    String inputId = recipesSection.getString(key + ".Input.Material", "BARRIER");
                    ConfigurationSection resultsSection = recipesSection.getConfigurationSection(key + ".Result");
                    if (resultsSection != null) {
                        CustomStonecuttingRecipe bRecipe = recipes.getOrDefault(inputId, new CustomStonecuttingRecipe(inputId));
                        for (String resultKey : resultsSection.getKeys(false)) {
                            String resultId = resultsSection.getString(resultKey + ".Material", "BARRIER");
                            int resultAmount = resultsSection.getInt(resultKey + ".Amount");
                            bRecipe.addResult(resultId, resultAmount);
                            rcount++;
                            if (config.getBoolean("General.Log_All_Recipes", false)) {
                                Bukkit.getLogger().info(ChatColor.GREEN + " Loaded recipe: " + inputId + " -> " + resultAmount + "x " + resultId);
                            }
                        }
                        recipes.put(inputId, bRecipe);
                    }
                } catch (Exception e) {
                    Bukkit.getLogger().severe(ChatColor.RED + "Error loading recipe: " + key);
                    e.printStackTrace();
                }
            }
            Bukkit.getLogger().info(ChatColor.GREEN + "Loaded total of " + rcount + " custom recipes.");
        }
    }


}
