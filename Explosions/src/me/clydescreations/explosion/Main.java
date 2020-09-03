package me.clydescreations.explosion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    public Main() {
    }

    public void onDisable() {
        super.onDisable();
    }

    public void onEnable() {
        super.onEnable();
    }

    public void runExplosion(Boolean running) {
        if (running) {
            Bukkit.broadcastMessage("Let the explosions begin");
            int minute = 1200;
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    Player player = randomPlayer(getPlayers());
                    warnPlayers(getPlayers());
                    explosion(player, getPlayers());
                }
            },0L, minute);
        } else {
            Bukkit.broadcastMessage("Explosions have stopped");
            getServer().getScheduler().cancelTasks(this);
        }
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>();
        players = (List)Bukkit.getOnlinePlayers();
        return players;
    }

    public Player randomPlayer(List<Player> players) {
        if (players.size() > 0) {
            Random r = new Random();
            Player randomPlayer = (Player)players.get(r.nextInt(players.size()));
            return randomPlayer;
        } else {
            return null;
        }
    }

    public void warnPlayers(List<Player> players) {
        players.forEach((player) -> {
            player.sendMessage("" + ChatColor.GOLD + "Tick... Tick...");
            player.playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1.0F, 1.0F);
        });
    }

    public void explosion(Player selectedPlayer, List<Player> players) {
        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                if (!selectedPlayer.isDead()) {

                    String currentWorld = selectedPlayer.getWorld().toString();

                    double x = selectedPlayer.getLocation().getX();
                    double y = selectedPlayer.getLocation().getY();
                    double z = selectedPlayer.getLocation().getZ();
                    List<String> explosions = new ArrayList<>();
                    String world = "minecraft:overworld";

                    if(currentWorld.contains("nether")){
                        world = "minecraft:the_nether";
                    }else if (currentWorld.contains("end")) {
                        world = "minecraft:the_end";
                    }else {
                        world = "minecraft:overworld";
                    }

                    String explosion_1 = "execute in " + world + " run summon minecraft:creeper " + x + " " + y + " " + z + " {powered: 1, Fuse: 0, CustomName: \"\\\"" + ChatColor.DARK_RED + "THE NUKE\\\"\"} ";
                    String explosion_2 = "execute in " + world + " run summon minecraft:creeper " + x + " " + y + " " + z + " {Fuse: 0, CustomName: \"\\\"" + ChatColor.DARK_RED + "THE SURPRISE\\\"\"} ";
                    String explosion_3 = "execute in " + world + " run summon minecraft:creeper " + x + " " + y + " " + z + " {Fuse: 1, CustomName: \"\\\"" + ChatColor.DARK_RED + "THE SURPRISE\\\"\"} ";
                    String explosion_4 = "execute in " + world + " run summon minecraft:tnt " + x + " " + y + " " + z + " {Primed: 1, CustomName: \"\\\"" + ChatColor.DARK_RED + "THE TIME BOMB\\\"\"} ";

                    explosions.add(explosion_1);
                    explosions.add(explosion_2);
                    explosions.add(explosion_3);
                    explosions.add(explosion_4);

                    Random exp = new Random();
                    String randomExplosion = (String)explosions.get(exp.nextInt(explosions.size()));
                    getServer().dispatchCommand(getServer().getConsoleSender(), randomExplosion);
                }

                players.forEach((player) -> {
                    player.sendMessage("" + ChatColor.RED + "BOOM!");
                });
            }
        }, 100L);
    }

    public void setScoreboard() {
        getServer().dispatchCommand(getServer().getConsoleSender(), "time set day");
        getServer().dispatchCommand(getServer().getConsoleSender(), "weather clear");
        getServer().dispatchCommand(getServer().getConsoleSender(), "scoreboard objectives add deaths deathCount");
        getServer().dispatchCommand(getServer().getConsoleSender(), "scoreboard objectives setdisplay list deaths");
    }

    public BossBar bossBar(Boolean action) {
        BossBar timerBar = Bukkit.createBossBar("00:00:00 ", BarColor.WHITE, BarStyle.SOLID);
        if(action) {
            getPlayers().forEach(timerBar::addPlayer);
        }else {
            getPlayers().forEach(timerBar::removePlayer);
        }
        Bukkit.broadcastMessage(timerBar.getPlayers().toString());

        return timerBar;
    }

    //Global time variables
    int hr = 0;
    int min = 0;
    int sec = 0;

    public void timer(BossBar timerBar) {

       Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

            @Override
            public void run() {

                ++sec;

                if(sec>59){
                    sec = 0;
                    ++min;
                }
                if(min>59){
                    min = 0;
                    ++hr;
                }

                String time;
                if(hr<10){
                    if(min<10){
                        if(sec<10){
                            time = "0"+hr+":0"+min+":0"+sec;
                        }else {
                            time = "0"+hr+":0"+min+":"+sec;
                        }
                    }else {
                        time = "0"+hr+":"+min+":"+sec;
                    }
                }else {
                    time = hr+":"+min+":"+sec;
                }
                timerBar.setTitle("Challenge Time - " + time);
                getPlayers().forEach(timerBar::addPlayer);
            }
        },0L, 20);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("Ex-start")) {
            setScoreboard();
            timer(bossBar(true));
            runExplosion(true);
            return true;
        }
        if (label.equalsIgnoreCase("Ex-stop")) {
            if(sender instanceof Player){
                runExplosion(false);
                timer(bossBar(false));
                getServer().getScheduler().cancelTasks(this);
            }else {
                return true;
            }
            return true;
        }
        return false;
    }
}

