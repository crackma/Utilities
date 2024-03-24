package me.crackma.utilities.commands;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.user.User;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class TestCommand implements CommandExecutor {
  private UtilitiesPlugin plugin;
  public TestCommand(UtilitiesPlugin plugin) {
    this.plugin = plugin;
    plugin.getCommand("test").setExecutor(this);
  }
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (!(sender instanceof Player)) return true;
    Player player = (Player) sender;
    User user = plugin.getUserManager().get(player.getUniqueId());
    for (Map.Entry<String, Boolean> set : user.getRank().getPermissions().entrySet()) {
      sender.sendMessage(
          "permission: " + set.getKey() + "\n" +
          "rank: " + set.getValue() + "\n" +
          "haspermission: " + player.hasPermission(set.getKey()) + "\n-------------------------------------");
    }
    return true;
  }
}
