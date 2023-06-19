package com.teenkung.devstonecutter;

import com.teenkung.devstonecutter.Builder.GuiBuilder;
import com.teenkung.devstonecutter.Listener.CommandListener;
import com.teenkung.devstonecutter.Listener.StoneCutterListener;
import com.teenkung.devstonecutter.Manager.RecipeManager;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.PluginCommand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class DevStoneCutter extends JavaPlugin {

    private ConfigLoader loader;
    private RecipeManager recipeManager;
    private GuiBuilder guiBuilder;

    @Override
    public void onEnable() {
        loader = new ConfigLoader(this);
        loader.loadConfig();
        recipeManager = new RecipeManager(this);
        recipeManager.loadDefaultRecipes();
        recipeManager.loadConfigRecipes();

        guiBuilder = new GuiBuilder(this);
        guiBuilder.loadGUI();

        Bukkit.getPluginManager().registerEvents(new StoneCutterListener(this), this);
        PluginCommand command = getCommand("stonecutter");
        if (command != null) {
            command.setExecutor(new CommandListener(this));
        }

    }

    @Override
    public void onDisable() {
        recipeManager.clearRecipes();
    }

    public GuiBuilder getGuiBuilder() { return guiBuilder; }
    public RecipeManager getRecipeManager() { return recipeManager; }
    public ConfigLoader getConfigLoader() { return loader; }

    public String turnToString(ItemStack stack) {
        CustomStack cs = CustomStack.byItemStack(stack);
        if (cs == null) {
            return stack.getType().toString();
        } else {
            return cs.getNamespacedID();
        }
    }

    public ItemStack getItemFromString(String stack, int resultAmount) {
        if (CustomStack.isInRegistry(stack)) {
            ItemStack item = CustomStack.getInstance(stack).getItemStack();
            item.setAmount(resultAmount);
            return item;
        } else {
            if (Material.getMaterial(stack) == null) { return null; }
            return new ItemStack(Material.getMaterial(stack), resultAmount);
        }
    }


}
