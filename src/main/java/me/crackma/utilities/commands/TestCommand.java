package me.crackma.utilities.commands;

import me.crackma.utilities.UtilitiesPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class TestCommand implements CommandExecutor {
    public TestCommand(UtilitiesPlugin plugin) {
        plugin.getCommand("test").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage("hi!");
        return true;
    }
}
