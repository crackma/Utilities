package me.crackma.utilities.punishments;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.user.User;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class PunishCommand implements CommandExecutor {
    private final UtilitiesPlugin plugin;
    public PunishCommand(UtilitiesPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("ban").setExecutor(this);
        plugin.getCommand("mute").setExecutor(this);
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (args.length < 3) return false;
        OfflinePlayer receiver = Bukkit.getOfflinePlayer(args[0]);
        User receiverUser = plugin.getUserManager().get(receiver.getUniqueId());
        if (receiverUser == null) return false;
        String end = String.valueOf(args[1].charAt(args[1].length() - 1));
        long duration;
        try {//parse number of args[1]
            duration = Long.parseLong(args[1].replace(end, ""));
        } catch (NumberFormatException exception) {
            return false;
        }
        switch (end) {//convert end of args[1] to ms
            case "s":
                duration = Duration.of(duration, ChronoUnit.SECONDS).toMillis();
                break;
            case "h":
                duration = Duration.of(duration, ChronoUnit.HOURS).toMillis();
                break;
            case "d":
                duration = Duration.of(duration, ChronoUnit.DAYS).toMillis();
                break;
            case "m":
                duration = Duration.of(duration, ChronoUnit.MONTHS).toMillis();
                break;
            case "y":
                duration = Duration.of(duration, ChronoUnit.YEARS).toMillis();
                break;
            default:
                return false;
        }
        PunishmentType punishmentType;
        if (label.equalsIgnoreCase("ban")) {
            punishmentType = PunishmentType.BAN;
        } else {
            punishmentType = PunishmentType.MUTE;
        }
        Player issuer = (Player) sender;
        User issuerUser = plugin.getUserManager().get(issuer.getUniqueId());
        long rightNow = new Date().getTime();
        Punishment punishment = new Punishment(issuer.getName(), punishmentType,
                plugin.getUtils().convertArray(args, " ", 2), rightNow, rightNow + duration);
        TextComponent cyan = new TextComponent("§b");
        TextComponent hasIssuedAPunishmentTo = new TextComponent(" §fhas issued a §b§o" + punishmentType.name() + " §fto ");
        TextComponent dot = new TextComponent("§f.");
        BaseComponent[] baseComponent = {cyan,
                issuerUser.getInfoTextComponent(),
                hasIssuedAPunishmentTo,
                cyan,
                receiverUser.getInfoTextComponent(),
                dot};
        plugin.getUserManager().issuePunishment(receiverUser, punishment);
        plugin.getUserDatabase().updateOne(receiverUser);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!sender.hasPermission("utilities.staff")) continue;;
            player.spigot().sendMessage(baseComponent);
        }
        return true;
    }
}
