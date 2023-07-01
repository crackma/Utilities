package me.crackma.utilities.commands;

import me.crackma.utilities.UtilitiesPlugin;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RulesCommand implements CommandExecutor {
	private String rules;
    public RulesCommand(UtilitiesPlugin plugin) {
        plugin.getCommand("rules").setExecutor(this);
        StringBuilder stringBuilder = new StringBuilder();
        for (String rule : plugin.getConfig().getStringList("rules")) {
        	stringBuilder.append(rule + "\n");
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    	sender.sendMessage(rules);
        return true;
    }
}
