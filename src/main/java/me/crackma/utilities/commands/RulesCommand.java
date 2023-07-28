package me.crackma.utilities.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.crackma.utilities.UtilitiesPlugin;
import net.md_5.bungee.api.ChatColor;

public class RulesCommand implements CommandExecutor {
	private String rules;
    public RulesCommand(UtilitiesPlugin plugin) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String rule : plugin.getConfig().getStringList("rules")) {
        	stringBuilder.append(ChatColor.translateAlternateColorCodes('&', rule) + "\n");
        }
        rules = stringBuilder.toString();
        if (rules == null) return;
        plugin.getCommand("rules").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	sender.sendMessage(rules);
        return true;
    }
}
