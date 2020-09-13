package me.Clydescreations.sample.inventory;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class Tutorial implements InventoryHolder {

    private Inventory gui;

    public Tutorial () {
        gui = Bukkit.createInventory(this, 9, ChatColor.translateAlternateColorCodes('&', "Test Run"));
    }

    @Override
    public org.bukkit.inventory.Inventory getInventory() {
        return gui;
    }
}
