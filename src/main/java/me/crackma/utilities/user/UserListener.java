package me.crackma.utilities.user;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.punishments.Punishment;
import me.crackma.utilities.rank.RankManager;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

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
        userDatabase.insert(uuid).thenCompose(unused -> userDatabase.get(uuid).thenAccept(user -> {
        	if (user.getRank() == null) user.setRank(rankManager.getPrimaryRank());
            if (userManager.get(uuid) == null) userManager.add(user);
        }));
    }
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        userManager.updateHiddenView();
        User user = userManager.get(player.getUniqueId());
        userManager.updateOne(user);
        if (user == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER,"§cFailed to get user instance.");
            return;
        }
        Punishment activeBan = user.findActiveBan();
        if (activeBan != null) event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cYou are currently banned for " + activeBan.getFormattedExpiryDate() + ".");
        if (player.hasPlayedBefore()) return;
        player.teleport(player.getWorld().getSpawnLocation());
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
    	event.setJoinMessage(null);
        Player player = event.getPlayer();
        User user = userManager.get(player.getUniqueId());
        user.setOfflinePlayer(player);
        userManager.updateOne(user);
        if (player.hasPlayedBefore()) return;
        Bukkit.broadcastMessage("§7Welcome §b" + player.getName() + " §7to the server!");
    }
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        User user = userManager.get(player.getUniqueId());
        if (user == null) {
            player.sendMessage("§cFailed to get user instance.");
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
    	attacker.sendMessage("§cYou cannot attack " + victim.getName() + " while he's in Mod Mode.");
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
    	event.setQuitMessage(null);
    }
}
