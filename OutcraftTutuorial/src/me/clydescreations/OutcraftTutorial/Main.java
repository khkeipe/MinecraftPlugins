package me.clydescreations.OutcraftTutorial;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

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
        Bukkit.addRecipe(getRecipe());
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player p = event.getPlayer();

        boolean hasPlayed = p.hasPlayedBefore();

        String welcomMessage = getConfig().getString("WELCOME.MESSAGE").replace("[","").replace("]","").replace("{player}", event.getPlayer().getDisplayName());

        if(!hasPlayed) {
            createGUI(p);
            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', welcomMessage));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent click){
        if(!click.getInventory().equals(gui)) return;
        if(click.getCurrentItem() == null) return;
        if(click.getCurrentItem().getItemMeta() == null) return;

        Player player = (Player) click.getWhoClicked();

            try {
                if(player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItemNaturally(player.getLocation(),getBook());
                }else {
                    player.getInventory().addItem(getBook());
                }
                player.getOpenInventory().close();
                click.setCancelled(true);
            } catch (Exception e) {
                e.printStackTrace();
                player.sendMessage( "" + ChatColor.RED + "Oops, something went wrong. Please contact the devs!");
            }
    }



    private ItemStack getBook(){
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);

        String bookTitle = getConfig().get("BOOK.TITLE").toString().replace("[","").replace("]","");;
        String bookAuthor = getConfig().get("BOOK.AUTHOR").toString().replace("[","").replace("]","");;
        List<String> bookPages = getConfig().getStringList("BOOK.PAGES");
        bookPages.forEach(page -> ChatColor.translateAlternateColorCodes('&', page).replace("[","").replace("]",""));

        BookMeta bookMeta = (BookMeta) book.getItemMeta();

        try{
            bookMeta.setTitle(ChatColor.translateAlternateColorCodes('&', bookTitle));
            bookMeta.setAuthor(ChatColor.translateAlternateColorCodes('&',bookAuthor));
            bookMeta.setPages(bookPages);

        }catch (Exception e) {
            e.printStackTrace();
        }

        book.setItemMeta(bookMeta);

        return book;
    }


    public void createGUI(Player player) {

        getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                String guiTitle = getConfig().get("GUI.TITLE").toString().replace("[","").replace("]","");
                gui = Bukkit.createInventory(null, 9, ChatColor.translateAlternateColorCodes('&', guiTitle));
                gui.setItem(4, getBook());

                if (player != null) {
                    player.openInventory(gui);
                }
            }
        },5L);

    }

    public ShapelessRecipe getRecipe() {

        NamespacedKey key = new NamespacedKey(this, "tutorial_book");

        ShapelessRecipe recipe = new ShapelessRecipe(key, getBook());

        recipe.addIngredient(Material.PURPLE_CARPET);
        recipe.addIngredient(Material.BOOK);

        return recipe;
    }
}
