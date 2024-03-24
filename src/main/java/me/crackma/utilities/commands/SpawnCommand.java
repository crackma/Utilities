package me.crackma.utilities.commands;

import me.crackma.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {
  public SpawnCommand(UtilitiesPlugin plugin) {
    plugin.getCommand("spawn").setExecutor(this);
  }
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length == 0) {
      if (!(sender instanceof Player)) return false;
      Player player = (Player) sender;
      Location loc = player.getWorld().getSpawnLocation();
      loc.setX(loc.getX() + 0.5);
      loc.setZ(loc.getZ() + 0.5);
      player.teleport(loc);
      return true;
    }
    if (!sender.hasPermission("utilities.spawn.others")) {
      sender.sendMessage("§cYou cannot do that.");
      return true;
    }
    Player player = Bukkit.getPlayer(args[0]);
    if (player == null) return false;
    Location loc = player.getWorld().getSpawnLocation();
    loc.setX(loc.getX() + 0.5);
    loc.setZ(loc.getZ() + 0.5);
    player.teleport(loc);
    return true;
  }
}
