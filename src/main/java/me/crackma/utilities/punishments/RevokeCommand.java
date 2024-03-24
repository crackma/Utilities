package me.crackma.utilities.punishments;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.user.User;
import net.md_5.bungee.api.chat.TextComponent;

public class RevokeCommand implements CommandExecutor {
  private UtilitiesPlugin plugin;
  public RevokeCommand(UtilitiesPlugin plugin) {
    this.plugin = plugin;
    plugin.getCommand("unban").setExecutor(this);
    plugin.getCommand("unmute").setExecutor(this);
  }
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length > 1) return false;
    OfflinePlayer player = Bukkit.getOfflinePlayer(args[0]);
    User user = plugin.getUserManager().get(player.getUniqueId());
    if (user == null) {
      sender.sendMessage("§7User not cached, searching in the database...");
      plugin.getUserDatabase().get(player.getUniqueId()).thenAccept(databaseUser -> {
        plugin.getUserManager().add(databaseUser);
      });
      user = plugin.getUserManager().get(player.getUniqueId());
      if (user == null) {
        sender.sendMessage("§cUser not found.");
        return true;
      }
    }
    Punishment punishment;
    if (label.equalsIgnoreCase("unban")) {
      punishment = user.findActiveBan();
    } else {
      punishment = user.findActiveMute();
    }
    if (punishment == null) {
      sender.sendMessage("§cCouldn't find any active punishments for " + args[0] + ".");
      return true;
    }
    punishment.setRevoked(true);
    plugin.getUserDatabase().updateOne(user);
    TextComponent message = new TextComponent("§fRevoked a §b§o" + punishment.getType() + " §ffrom ");
    TextComponent dot = new TextComponent("§f.");
    sender.spigot().sendMessage(message, user.getInfoTextComponent(), dot);
    return true;
  }
}
