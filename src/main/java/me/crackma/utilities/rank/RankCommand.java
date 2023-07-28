package me.crackma.utilities.rank;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.user.User;
import me.crackma.utilities.user.UserDatabase;
import me.crackma.utilities.user.UserManager;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class RankCommand implements CommandExecutor, TabCompleter {
    private RankManager rankManager;
    private UserManager userManager;
    private UserDatabase userDatabase;
    public RankCommand(UtilitiesPlugin plugin) {
        plugin.getCommand("rank").setExecutor(this);
        plugin.getCommand("rank").setTabCompleter(this);
        rankManager = plugin.getRankManager();
        userManager = plugin.getUserManager();
        userDatabase = plugin.getUserDatabase();
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return false;
        User user;
        Rank rank;
        switch (args[0].toLowerCase()) {
            case "help":
                sender.sendMessage("§c/rank create [<name>] [<team>]§f\n" +
                                   "§c/rank permission [<rank>] [<permission>] true/false§f\n" +
                                   "§c/rank meta setPrefix/setSuffix/setTeam [<rank>] [<args>]§f\n" +
                                   "§c/rank set [<user>] [<rank>]§f\n" +
                                   "§c/rank setPrimary [<rank>]§f\n" +
                                   "§c/rank remove [<rank>]§f\n" + 
                                   "§c/rank reload");
                return true;
            case "create":
                if (args.length < 3) break;
                rank = rankManager.get(args[1]);
                if (rank != null) break;
                rank = new Rank(args[1], "", "", ChatColor.WHITE, rankManager.getScoreboard().registerNewTeam(args[2]));
                rankManager.create(new Rank(args[1], "", "", ChatColor.WHITE, rankManager.getScoreboard().registerNewTeam(args[2])));
                sender.sendMessage("§fCreated rank §b" + args[1] + "§f.");
                return true;
            case "permission":
                if (args.length < 4) break;
                rank = rankManager.get(args[1]);
                if (rank == null) break;
                rank.setPermission(args[2], Boolean.valueOf(args[3]));
                rankManager.updatePermissions(rank);
                userManager.updateMany(rank);
                sender.sendMessage("§fSet permission §b" + args[2] + " §fof rank §b" + rank.getName() + " §fto §b" + args[3] + "§f.");
                return true;
            case "meta":
                if (args.length < 3) break;
                rank = rankManager.get(args[2]);
                if (rank == null) break;
                switch (args[1].toLowerCase()) {
                    case "setprefix":
                        if (args.length < 4) {
                            rank.setPrefix("");
                            rankManager.updatePrefix(rank);
                            sender.sendMessage("§fCleared rank §b" + rank.getName() + "'s §fprefix.");
                            return true;
                        }
                        rank.setPrefix(convertArray(args, " ", 3));
                        rankManager.updatePrefix(rank);
                        userManager.updateMany(rank);
                        sender.sendMessage("§fSet rank §b" + rank.getName() + "'s §fprefix to §b" + rank.getPrefix() + "§f.");
                        return true;
                    case "setsuffix":
                        if (args.length < 4) {
                            rank.setSuffix("");
                            rankManager.updatePrefix(rank);
                            sender.sendMessage("§fCleared rank §b" + rank.getName() + "'s §fprefix.");
                            return true;
                        }
                        rank.setSuffix(convertArray(args, " ", 3));
                        rankManager.updateSuffix(rank);
                        userManager.updateMany(rank);
                        sender.sendMessage("§fSet rank §b" + rank.getName() + "'s §fsuffix to §b" + rank.getSuffix() + "§f.");
                        return true;
                    case "setteam":
                        if (args.length < 4) break;
                        if (rank.getTeam() != null) rank.getTeam().unregister();
                        rank.setTeam(rankManager.getScoreboard().registerNewTeam(args[3]));
                        rankManager.updateTeam(rank);
                        userManager.updateMany(rank);
                        sender.sendMessage("§fSet rank §b" + rank.getName() + "'s §fteam to §b" + rank.getTeam().getName() + "§f.");
                        return true;
                    default:
                        break;
                }
                userManager.updateMany(rank);
                return true;
            case "set":
                if (args.length < 2) break;
                OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
                if (player == null) break;
                user = userManager.get(player.getUniqueId());
                if (user == null) break;
                rank = rankManager.get(args[2]);
                if (rank == null) break;
                user.getRank().getTeam().removeEntry(player.getName());
                user.setRank(rank);
                userManager.updateOne(user);
                userManager.updateHiddenView();
                userDatabase.updateOne(user);
                sender.sendMessage("§fSet §b" + player.getName() + "'s §frank to §b" + rank.getName() + "§f.");
                return true;
            case "setprimary":
                if (args.length < 2) break;
                rank = rankManager.get(args[1]);
                if (rank == null) break;
                rankManager.setPrimaryRank(rank);
                sender.sendMessage("§fSet the primary rank to §b" + rank.getName() + "§f.");
                return true;
            case "remove":
                if (args.length < 2) break;
                rank = rankManager.get(args[1]);
                if (rank == null) break;
                if (rank == rankManager.getPrimaryRank()) {
                    sender.sendMessage("§cYou cannot remove the primary rank.");
                    return true;
                }
                rankManager.remove(rank.getName());
                userManager.updateMany(rank);
                userManager.updateHiddenView();
                userDatabase.updateMany(rank);
                sender.sendMessage("§fRemoved rank §b" + rank.getName() + "§f.");
                return true;
            case "reload":
            	rankManager.unload();
            	rankManager.load();
            	sender.sendMessage("§fReloaded all ranks.");
            	return true;
            default:
                break;
        }
        return false;
    }
    public String convertArray(String[] stringArray, String delimiter, int exclude) {
        StringBuilder stringBuilder = new StringBuilder();
        int current = 0;
        for (String string : stringArray) {
            current++;
            if (current <= exclude) continue;
            if (current == stringArray.length) {
                stringBuilder.append(string);
                return stringBuilder.toString();
            }
            stringBuilder.append(string).append(delimiter);
        }
        return stringBuilder.toString();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("create");
            completions.add("permission");
            completions.add("meta");
            completions.add("set");
            completions.add("setPrimary");
            completions.add("remove");
            completions.add("reload");
            return completions;
        }
        switch (args[0].toLowerCase()) {
            //rank create [<name>] [<team>]
            case "create":
                if (args.length == 2) completions.add("[<name>]");
                if (args.length == 3) completions.add("[<team>]");
                break;
            //rank permission [<rank>] [<permission>] true/false
            case "permission":
                if (args.length == 2) rankManager.getRanks().forEach(rank -> completions.add(rank.getName()));
                if (args.length == 3) completions.add("[<permission>]");
                if (args.length == 4) {
                    completions.add("true");
                    completions.add("false");
                }
                break;
            //rank meta setPrefix/setSuffix/setTeam [<rank>] [<args>]
            case "meta":
                if (args.length == 2) {
                    completions.add("setPrefix");
                    completions.add("setSuffix");
                    completions.add("setTeam");
                }
                if (args.length == 3) rankManager.getRanks().forEach(rank -> completions.add(rank.getName()));
                if (args.length == 4) {
                	Rank rank = rankManager.get(args[2]);
                	if (rank == null) break;
                	if (args[1].equalsIgnoreCase("setprefix")) completions.add(rank.getPrefix());
                	if (args[1].equalsIgnoreCase("setsuffix")) completions.add(rank.getSuffix());
                	if (args[1].equalsIgnoreCase("setteam")) completions.add(rank.getTeam().getName());
                }
                break;
            //rank grant [<user>] [<rank>]
            case "grant":
                if (args.length == 2) userManager.getUsers().forEach(user -> completions.add(user.getOfflinePlayer().getName()));
                if (args.length == 3) rankManager.getRanks().forEach(rank -> completions.add(rank.getName()));
                break;
            //rank setPrimary [<rank>]
            case "setprimary":
            //rank remove [<rank>]
            case "remove":
                if (args.length == 2) rankManager.getRanks().forEach(rank -> completions.add(rank.getName()));
                break;
        }
        return completions;
    }
}
