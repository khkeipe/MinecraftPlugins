package me.clydescreations.OutcraftTutorial;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class Main extends JavaPlugin implements Listener {

    public Inventory gui;

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        saveDefaultConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                List<String> players = playerConfig.getStringList("BOOK_GIVEN");
                String welcomMessage = getConfig().getString("WELCOME.MESSAGE").replace("[","").replace("]","").replace("{player}", event.getPlayer().getDisplayName());

                if(!players.contains(event.getPlayer().getUniqueId().toString())){
                    createGUI(event.getPlayer().getUniqueId());
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', welcomMessage));
                }

                if(!playerList.exists()){
                    try{
                        playerList.createNewFile();
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 5L);
    }

    private static File playerList = new File("plugins/OutCraft_Tutorial/players.yml");
    private static FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerList);

    @EventHandler
    public void onInventoryClick(InventoryClickEvent click){
        if(!click.getInventory().equals(gui)) return;
        if(click.getCurrentItem() == null) return;
        if(click.getCurrentItem().getItemMeta() == null) return;

        Player player = (Player) click.getWhoClicked();

        List<String> players = playerConfig.getStringList("BOOK_GIVEN");

        if(!players.contains(player.getUniqueId().toString())) {
            players.add(player.getUniqueId().toString());
            playerConfig.addDefault("BOOK_GIVEN", players);
            try {
                playerConfig.options().copyDefaults(true);
                playerConfig.save(playerList);
                player.getOpenInventory().close();
                player.updateInventory();
                player.getWorld().dropItemNaturally(player.getLocation(),book);
                click.setCancelled(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private  ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

    public void createGUI(UUID playerID) {

        String guiTitle = getConfig().get("GUI.TITLE").toString().replace("[","").replace("]","");
        String bookTitle = getConfig().get("BOOK.TITLE").toString().replace("[","").replace("]","");;
        String bookAuthor = getConfig().get("BOOK.AUTHOR").toString().replace("[","").replace("]","");;
        List<String> bookPages = getConfig().getStringList("BOOK.PAGES");
        bookPages.forEach(page -> ChatColor.translateAlternateColorCodes('&', page).replace("[","").replace("]",""));

        gui = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', guiTitle));

        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        try{
            bookMeta.setTitle(ChatColor.translateAlternateColorCodes('&', bookTitle));
            bookMeta.setAuthor(ChatColor.translateAlternateColorCodes('&',bookAuthor));
            bookMeta.setPages(bookPages);

        }catch (Exception e) {
            e.printStackTrace();
        }

        book.setItemMeta(bookMeta);

        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                gui.setItem(4, book);

                Player player = Bukkit.getPlayer(playerID);

                if (player != null) {
                    player.openInventory(gui);
                }
            }
        },1L);

    }
}
