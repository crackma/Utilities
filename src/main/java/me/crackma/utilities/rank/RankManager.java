package me.crackma.utilities.rank;

import lombok.Getter;
import me.crackma.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class RankManager {
    @Getter
    private Scoreboard scoreboard;
    private UtilitiesPlugin plugin;
    @Getter
    private Set<Rank> ranks = new HashSet<>();
    private File file;
    private FileConfiguration configuration;
    @Getter
    private Rank primaryRank;
    public RankManager(UtilitiesPlugin plugin) {
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.plugin = plugin;
        file = new File(plugin.getDataFolder(), "ranks.yml");
        file.getParentFile().mkdirs();
        plugin.saveResource("ranks.yml", false);
        configuration = new YamlConfiguration();
        try {
            configuration.load(file);
        } catch (IOException | InvalidConfigurationException exception) {
            exception.printStackTrace();
        }
        load();
    }
    public void saveConfiguration() {
        try {
            configuration.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public void load() {
        for (String rankName : configuration.getConfigurationSection("").getKeys(false)) {
            if (rankName.equalsIgnoreCase("primary")) continue;
            String prefix = configuration.getString(rankName + ".prefix");
            String suffix = configuration.getString(rankName + ".suffix");
            String nameColor = configuration.getString(rankName + ".nameColor");
            String team = configuration.getString(rankName + ".team");
            if (prefix == null || suffix == null || nameColor == null || team == null) {
            	Bukkit.getLogger().warning("Halted loading " + rankName + " rank since it's information is incomplete.");
            	continue;
            }
            Rank rank = new Rank(
                    rankName,
                    prefix,
                    suffix,
                    ChatColor.valueOf(nameColor),
                    scoreboard.registerNewTeam(team));
            for (String permission : configuration.getStringList("")) {
                String[] split = permission.split(",");
                rank.setPermission(split[0], Boolean.valueOf(split[1]));
            }
            ranks.add(rank);
        }
        if (configuration.getString("primary") == null) configuration.set("primary", "default");
        this.primaryRank = get(configuration.getString("primary"));
        if (primaryRank == null) {
            Bukkit.getLogger().info("No primary rank was found in ranks.yml, creating one.");
            Rank rank = new Rank("default", "", "", ChatColor.WHITE, scoreboard.registerNewTeam("a"));
            create(rank);
        }
    }
    public void unload() {
    	ranks.clear();
    	scoreboard.getTeams().forEach(team -> team.unregister());
    }
    public void setPrimaryRank(Rank rank) {
        primaryRank = rank;
        configuration.set("primary", rank.getName());
        saveConfiguration();
    }
    public void create(Rank rank) {
        ranks.add(rank);
        String name = rank.getName();
        configuration.set(name + ".prefix", rank.getPrefix());
        configuration.set(name + ".suffix", rank.getSuffix());
        configuration.set(name + ".nameColor", rank.getNameColor().name());
        configuration.set(name + ".team", rank.getTeam().getName());
        List<String> permissions = new ArrayList<>();
        for (Map.Entry<String, Boolean> set : rank.getPermissions().entrySet()) {
            permissions.add(set.getKey() + "," + set.getValue());
        }
        configuration.set(name + ".permissions", permissions);
        saveConfiguration();
    }
    public void updatePrefix(Rank rank) {
    	Team team = rank.getTeam();
    	team.setPrefix(rank.getPrefix());
        configuration.set(rank.getName() + ".prefix", rank.getPrefix());
        saveConfiguration();
    }
    public void updateSuffix(Rank rank) {
    	Team team = rank.getTeam();
    	team.setSuffix(rank.getSuffix());
        configuration.set(rank.getName() + ".suffix", rank.getSuffix());
        saveConfiguration();
    }
    public void updateNameColor(Rank rank) {
    	Team team = rank.getTeam();
    	team.setColor(rank.getNameColor());
    	configuration.set(rank.getName() + ".nameColor", rank.getNameColor().name());
    	saveConfiguration();
    }
    public void updateTeam(Rank rank) {
        Team team = rank.getTeam();
        team.setPrefix(rank.getPrefix());
        team.setSuffix(rank.getSuffix());
        team.setColor(rank.getNameColor());
        configuration.set(rank.getName() + ".team", team.getName());
        plugin.getUserManager().updateMany(rank);
        saveConfiguration();
    }
    public void updatePermissions(Rank rank) {
        List<String> permissions = new ArrayList<>();
        for (Map.Entry<String, Boolean> set: rank.getPermissions().entrySet()) {
            permissions.add(set.getKey() + "," + set.getValue());
        }
        configuration.set(rank.getName() + ".permissions", permissions);
        saveConfiguration();
    }
    public Rank get(String name) {
        for (Rank rank : ranks) {
            if (!rank.getName().equalsIgnoreCase(name)) continue;
            return rank;
        }
        return null;
    }
    public void remove(String name) {
        for (Rank rank : ranks) {
            if (!rank.getName().equalsIgnoreCase(name)) ranks.remove(rank);
        }
        configuration.set(name, null);
        saveConfiguration();
    }
}
