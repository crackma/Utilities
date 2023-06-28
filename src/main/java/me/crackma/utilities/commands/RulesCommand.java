package me.crackma.utilities.commands;

import me.crackma.utilities.UtilitiesPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RulesCommand implements CommandExecutor {
    public RulesCommand(UtilitiesPlugin plugin) {
        plugin.getCommand("rules").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        return true;
    }
}
