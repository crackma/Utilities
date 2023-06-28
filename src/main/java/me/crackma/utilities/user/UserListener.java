package me.crackma.utilities.user;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.punishments.Punishment;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;

public class UserListener implements Listener {
    private UserManager userManager;
    private UserDatabase userDatabase;
    public UserListener(UtilitiesPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        userManager = plugin.getUserManager();
        userDatabase = plugin.getUserDatabase();
    }
    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        userDatabase.insert(uuid).thenCompose(unused -> userDatabase.get(uuid).thenAccept(user -> {
            if (userManager.get(uuid) == null) userManager.add(user);
        }));
    }
    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        User user = userManager.get(player.getUniqueId());
        if (user == null) event.disallow(PlayerLoginEvent.Result.KICK_OTHER,"§cFailed to get user instance.");
        Punishment activeBan = user.findActiveBan();
        if (activeBan != null) event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cYou are currently banned for " + activeBan.getFormattedExpiryDate() + ".");
        if (player.hasPlayedBefore()) return;
        player.teleport(player.getWorld().getSpawnLocation());
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = userManager.get(player.getUniqueId());
        user.setPlayer(player);
        userManager.updateOne(user);
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
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        User user = userManager.get(event.getPlayer().getUniqueId());
        user.setPlayer(null);
    }
}
