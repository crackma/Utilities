package me.crackma.utilities.user;

import lombok.Getter;
import me.crackma.utilities.punishments.Punishment;
import me.crackma.utilities.punishments.PunishmentType;
import me.crackma.utilities.rank.Rank;
import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.rank.RankManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
        users.add(user);
    }
    public void issuePunishment(User user, Punishment punishment) {
        user.addPunishment(punishment);
        if (punishment.getType() == PunishmentType.BAN) {
            user.getPlayer().kickPlayer("§cYou have been banned for " + punishment.getFormattedExpiryDate() + ".");
        } else {
            user.getPlayer().sendMessage("§cYou have been muted for " + punishment.getFormattedExpiryDate() + ".");
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
        PermissionAttachment permissionAttachment = user.getPlayer().addAttachment(plugin);
        Rank rank = user.getRank();
        if (rank == null) user.setRank(rankManager.getPrimaryRank());
        rank = user.getRank();
        rank.getTeam().addEntry(user.getPlayer().getName());
        Player player = user.getPlayer();
        player.setDisplayName(user.getDisplayName());
        player.setPlayerListName(user.getDisplayName());
        for (Map.Entry<String, Boolean> set: rank.getPermissions().entrySet()) {
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
