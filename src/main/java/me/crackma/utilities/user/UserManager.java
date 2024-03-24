package me.crackma.utilities.user;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import lombok.Getter;
import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.punishments.Punishment;
import me.crackma.utilities.punishments.PunishmentType;
import me.crackma.utilities.rank.Rank;
import me.crackma.utilities.rank.RankManager;

public class UserManager {
    private UtilitiesPlugin plugin;
    private RankManager rankManager;
    @Getter
    private Set<User> users = new HashSet<>();
    public UserManager(UtilitiesPlugin plugin) {
        this.plugin = plugin;
        rankManager = plugin.getRankManager();
    }
    public void add(User user) {
    	if (user == null) return;
        users.add(user);
    }
    public void issuePunishment(User user, Punishment punishment) {
    	if (user == null) return;
        user.addPunishment(punishment);
        if (!(user.getOfflinePlayer() instanceof Player)) return;
        Player player = (Player) user.getOfflinePlayer();
        if (punishment.getType() == PunishmentType.BAN) {
            player.kickPlayer("§cYou have been banned for " + punishment.getFormattedExpiryDate() + ".");
        } else {
        	player.sendMessage("§cYou have been muted for " + punishment.getFormattedExpiryDate() + ".");
        }
    }
    public void toggleVanish(User user) {
    	if (user == null) return;
    	user.setVanished(!user.isVanished());
    	if (user.isVanished()) {
    		updateHiddenView();
    	} else {
    		updateHiddenView();
    	}
    }
    public void updateHiddenView() {
    	for (Player player : Bukkit.getOnlinePlayers()) {
    		if (player.hasPermission("utilities.staff")) continue;
    		for (User user : users) {
    			if (!(user.getOfflinePlayer() instanceof Player)) continue;
    			Player hiddenPlayer = (Player) user.getOfflinePlayer();
    			if (user.isVanished()) {
    				player.hidePlayer(plugin, hiddenPlayer);
    			} else {
    				player.showPlayer(plugin, hiddenPlayer);
    			}
    		}
    	}
    }
    public User get(UUID uniqueId) {
        for (User user : users) {
            if (!user.getUniqueId().equals(uniqueId)) continue;
            return user;
        }
        return null;
    }
    public void updateOne(User user) {
    	if (!(user.getOfflinePlayer() instanceof Player)) {
    		Bukkit.getLogger().warning("User's player isn't loaded.");
    		return;
    	}
      Player player = (Player) user.getOfflinePlayer();
      FileConfiguration configuration = plugin.getConfig();
      String header = ChatColor.translateAlternateColorCodes('&', configuration.getString("tab.header"));
      String footer = ChatColor.translateAlternateColorCodes('&', configuration.getString("tab.footer"));
      player.setPlayerListHeaderFooter(header, footer);
      player.setScoreboard(rankManager.getScoreboard());
      PermissionAttachment permissionAttachment = user.getPermissionAttachment();
      if (permissionAttachment != null) permissionAttachment.remove();
      permissionAttachment = player.addAttachment(plugin);
      user.setPermissionAttachment(permissionAttachment);
      Rank rank = user.getRank();
      try {
        rank.getTeam().addEntry(player.getName());
      } catch (IllegalStateException exception) {
        rank = rankManager.getPrimaryRank();
        user.setRank(rank);
        rank.getTeam().addEntry(player.getName());
      }
      for (Map.Entry<String, Boolean> set : rank.getPermissions().entrySet()) {
        permissionAttachment.setPermission(set.getKey(), set.getValue());
      }
    }
  public void updateMany(Rank rank) {
    for (User user : users) {
      if (user.getRank() != rank) continue;
      updateOne(user);
    }
  }
}
