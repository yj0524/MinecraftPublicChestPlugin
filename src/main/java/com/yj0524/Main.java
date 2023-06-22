package com.yj0524;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class Main extends JavaPlugin implements Listener {

    private Inventory publicChest;
    private int chestSize;

    @Override
    public void onEnable() {
        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        // Load chest size from config
        loadConfig();
        File cfile = new File(getDataFolder(), "config.yml");
        if (cfile.length() == 0) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        getLogger().info("Plugin Enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("Plugin Disabled.");
    }

    private void loadConfig() {
        // Load config
        FileConfiguration config = getConfig();
        chestSize = config.getInt("chestSize", 27);
        // Save config
        config.set("chestSize", chestSize);
        saveConfig();
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getOpenInventory().getTopInventory().equals(publicChest)) {
            event.setCancelled(true);
            player.sendMessage("You cannot break public chest!");
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(publicChest)) {
            // Save chest contents to config
            Inventory chest = event.getInventory();
            for (int i = 0; i < chest.getSize(); i++) {
                ItemStack item = chest.getItem(i);
                if (item != null) {
                    getConfig().set("publicChest." + i, item);
                } else {
                    getConfig().set("publicChest." + i, null);
                }
            }
            saveConfig();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (cmd.getName().equalsIgnoreCase("publicchest")) {
                if (publicChest == null) {
                    publicChest = Bukkit.createInventory(null, chestSize, "Public Chest");
                    for (int i = 0; i < chestSize; i++) {
                        ItemStack item = getConfig().getItemStack("publicChest." + i);
                        if (item != null) {
                            publicChest.setItem(i, item);
                        }
                    }
                }
                player.openInventory(publicChest);
                return true;
            }
        }
        return false;
    }
}
