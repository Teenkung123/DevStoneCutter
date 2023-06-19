package com.teenkung.devstonecutter.Listener;

import com.teenkung.devstonecutter.Builder.GuiBuilder;
import com.teenkung.devstonecutter.DevStoneCutter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener implements CommandExecutor {

    private final DevStoneCutter plugin;

    public CommandListener(DevStoneCutter plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
        } else if (args[0].equalsIgnoreCase("reload")) {
            long start = System.currentTimeMillis();
            sender.sendMessage(ChatColor.GREEN + "Reloading configuration and recipes.");
            plugin.getConfigLoader().loadConfig();
            plugin.getRecipeManager().loadDefaultRecipes();
            plugin.getRecipeManager().loadConfigRecipes();
            plugin.getGuiBuilder().loadGUI();
            sender.sendMessage(ChatColor.GREEN + "Reloaded configuration and recipes. " + ChatColor.YELLOW + "(" + (System.currentTimeMillis() - start) + "ms");
        } else if (args[0].equalsIgnoreCase("open")) {
            if (args.length == 2) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    player.openInventory(plugin.getGuiBuilder().buildGUI());
                }
            } else {
                if (sender instanceof Player player) {
                    player.openInventory(plugin.getGuiBuilder().buildGUI());
                }
            }
        } else {
            sender.sendMessage("Unknown command. Type \"/help\" for help.");
        }
        return false;
    }
}
