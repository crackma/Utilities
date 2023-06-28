package me.crackma.utilities.user.info;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.gui.GuiManager;
import me.crackma.utilities.user.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;

public class InfoCommand implements CommandExecutor, Listener {
    private UtilitiesPlugin plugin;
    public InfoCommand(UtilitiesPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("info").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (args.length < 1) {
            Player player = (Player) sender;
            User user = plugin.getUserManager().get(player.getUniqueId());
            plugin.getGuiManager().openGUI(new InfoGui(plugin, user), player);
            return true;
        }
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
        if (offlinePlayer == null) return false;
        Player player = (Player) sender;
        UUID uniqueId = offlinePlayer.getUniqueId();
        User targetUser = plugin.getUserManager().get(uniqueId);
        if (targetUser == null) {
            sender.sendMessage("§7User not cached, searching in the database...");
            plugin.getUserDatabase().get(uniqueId).thenAccept(databaseUser -> {
                if (databaseUser == null) {
                    sender.sendMessage("§cUser not found.");
                    return;
                }
                plugin.getGuiManager().openGUI(new InfoGui(plugin, databaseUser), player);
            });
        } else {
            plugin.getGuiManager().openGUI(new InfoGui(plugin, targetUser), player);
        }
        return true;
    }

}
