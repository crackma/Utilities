package me.crackma.utilities.commands;

import me.crackma.utilities.UtilitiesPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class DiscordCommand implements CommandExecutor {
    private final String discord;
    public DiscordCommand(UtilitiesPlugin plugin) {
        plugin.getCommand("discord").setExecutor(this);
        discord = plugin.getConfig().getString("discord");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(discord);
        return true;
    }
}
