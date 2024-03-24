package me.crackma.utilities.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.user.User;
import me.crackma.utilities.user.UserManager;

public class VanishCommand implements CommandExecutor {
  private UtilitiesPlugin plugin;
  public VanishCommand(UtilitiesPlugin plugin) {
    this.plugin = plugin;
    plugin.getCommand("vanish").setExecutor(this);
  }
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    UserManager userManager = plugin.getUserManager();
    OfflinePlayer offlinePlayer;
    if (args.length == 0) {
      if (!(sender instanceof Player)) return false;
      offlinePlayer = (OfflinePlayer) sender;
    } else {
      offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
    }
    User receiverUser = userManager.get(offlinePlayer.getUniqueId());
    userManager.toggleVanish(receiverUser);
    String vanish = receiverUser.isVanished() ? "vanished" : "unvanished";
    Bukkit.broadcast(receiverUser.getDisplayName() + " §fhas §b" + vanish + "§f.", "utilities.staff");
    return true;
  }
}
