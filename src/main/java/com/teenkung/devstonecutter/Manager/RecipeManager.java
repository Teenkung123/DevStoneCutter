package com.teenkung.devstonecutter.Manager;

import com.teenkung.devstonecutter.DevStoneCutter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.StonecuttingRecipe;

import java.util.HashMap;
import java.util.Iterator;

public class RecipeManager {

    private HashMap<String, CustomStonecuttingRecipe> stoneCutterRecipes;
    private final DevStoneCutter plugin;
    public RecipeManager(DevStoneCutter plugin) {
        this.plugin = plugin;
    }

    public void loadDefaultRecipes() {
        Iterator<Recipe> recipeIterator = Bukkit.recipeIterator();
        int rcount = 0;
        this.stoneCutterRecipes = new HashMap<>();
        while (recipeIterator.hasNext()) {
            Recipe recipe = recipeIterator.next();
            if (recipe instanceof StonecuttingRecipe rec) {
                CustomStonecuttingRecipe cr = stoneCutterRecipes.getOrDefault(plugin.turnToString(rec.getInput()), new CustomStonecuttingRecipe(plugin.turnToString(rec.getInput())));
                cr.addResult(plugin.turnToString(rec.getResult()), rec.getResult().getAmount());
                stoneCutterRecipes.put(plugin.turnToString(rec.getInput()), cr);
                if (plugin.getConfig().getBoolean("General.Log_All_Recipes", false)) {
                    Bukkit.getLogger().info(ChatColor.GREEN + " Loaded recipe: " + ((StonecuttingRecipe) recipe).getInput() + " -> " + recipe.getResult().getAmount() + "x " + recipe.getResult());
                }
                rcount++;
            }
        }
        Bukkit.getLogger().info(ChatColor.GREEN + "Loaded total of " + rcount + " vanilla recipes.");
    }

    public void loadConfigRecipes() {
        for (String key : plugin.getConfigLoader().getRecipes().keySet()) {
            if (stoneCutterRecipes.containsKey(key)) {
                CustomStonecuttingRecipe cr = stoneCutterRecipes.get(key);
                for (String result : plugin.getConfigLoader().getRecipes().get(key).getResult()) {
                    cr.addResult(result, plugin.getConfigLoader().getRecipes().get(key).getResultAmount(result));
                }
            } else {
                stoneCutterRecipes.put(key, plugin.getConfigLoader().getRecipes().get(key));
            }
        }
    }

    public HashMap<String, CustomStonecuttingRecipe> getStoneCutterRecipes() {
        return stoneCutterRecipes;
    }

    public void clearRecipes() {
        stoneCutterRecipes.clear();
    }

}
