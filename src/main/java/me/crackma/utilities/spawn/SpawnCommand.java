package me.crackma.utilities.spawn;

import me.crackma.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
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
            player.teleport(player.getWorld().getSpawnLocation());
            return true;
        }
        if (!sender.hasPermission("utilities.spawn.others")) {
            sender.sendMessage("§cYou cannot do that.");
            return true;
        }
        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) return false;
        player.teleport(player.getWorld().getSpawnLocation());
        return true;
    }
}
