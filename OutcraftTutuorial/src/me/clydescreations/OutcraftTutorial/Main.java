package me.clydescreations.OutcraftTutorial;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.EventListener;

public class Main extends JavaPlugin implements EventListener {

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.saveDefaultConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        FileConfiguration data = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "data.yml"));
        data.set("player", event.getPlayer().getDisplayName().toString());

    }

}
