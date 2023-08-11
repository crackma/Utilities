package me.crackma.utilities.user;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.punishments.Punishment;
import me.crackma.utilities.rank.RankManager;
import net.md_5.bungee.api.chat.TextComponent;

public class UserListener implements Listener {
    private UserManager userManager;
    private UserDatabase userDatabase;
    private RankManager rankManager;
    public UserListener(UtilitiesPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        userManager = plugin.getUserManager();
        userDatabase = plugin.getUserDatabase();
        rankManager = plugin.getRankManager();
    }
    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
    	UUID uuid = event.getUniqueId();
    	User user = userManager.get(uuid);
    	if (user == null) {
    		user = userDatabase.getOrCreate(uuid);
    		userManager.add(user);
    	}
        if (user.getRank() == null) user.setRank(rankManager.getPrimaryRank());
    }
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        User user = userManager.get(uuid);
        if (user == null) { event.disallow(PlayerLoginEvent.Result.KICK_OTHER,"§cCouldn't get user instance."); return; }
	    Punishment activeBan = user.findActiveBan();
        if (activeBan != null) { event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cYou are currently banned for " + activeBan.getFormattedExpiryDate() + "."); return; }
        user.setOfflinePlayer(player);
	    userManager.updateHiddenView();
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
    	event.setJoinMessage(null);
        Player player = event.getPlayer();
        User user = userManager.get(player.getUniqueId());
        userManager.updateOne(user);
        if (player.hasPlayedBefore()) return;
        Location loc = player.getWorld().getSpawnLocation();
        loc.setX(loc.getX() + 0.5);
        loc.setZ(loc.getZ() + 0.5);
        player.teleport(loc);
        Bukkit.broadcastMessage(user.getDisplayName() + " §fhas joined the server for the first time!");
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
    	event.setQuitMessage(null);
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        User user = userManager.get(player.getUniqueId());
        if (user == null) {
            player.sendMessage("§cFailed to get your user instance, please try to rejoin the server.");
            return;
        }
        Punishment activeMute = user.findActiveMute();
        if (activeMute != null) {
            player.sendMessage("§cYou are currently muted for " + activeMute.getFormattedExpiryDate() + ".");
            return;
        }
        TextComponent message = new TextComponent("§f: " + event.getMessage());
        TextComponent[] array = {user.getInfoTextComponent(), message};
        Bukkit.getServer().spigot().broadcast(array);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
    	if (!(event.getEntity() instanceof Player)) return;
    	Player victim = (Player) event.getEntity();
    	User victimUser = userManager.get(victim.getUniqueId());
    	if (!victimUser.isModMode()) return;
    	event.setCancelled(true);
    	if (!(event instanceof EntityDamageByEntityEvent)) return;
    	EntityDamageByEntityEvent newEvent = (EntityDamageByEntityEvent) event;
    	if (!(newEvent.getDamager() instanceof Player)) return;
    	Player attacker = (Player) newEvent.getDamager();
    	attacker.sendMessage("§cYou cannot attack " + victim.getName() + ".");
    }
}
