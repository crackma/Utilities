package me.crackma.utilities.rank;

import lombok.Getter;
import me.crackma.utilities.UtilitiesPlugin;
import org.bukkit.Bukkit;
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
        for (String rankName : configuration.getConfigurationSection("").getKeys(false)) {
            if (rankName.equalsIgnoreCase("primary")) continue;
            Rank rank = new Rank(
                    rankName,
                    configuration.getString(rankName + ".prefix"),
                    configuration.getString(rankName + ".suffix"),
                    scoreboard.registerNewTeam(configuration.getString(rankName + ".team")));
            Team team = rank.getTeam();
            team.setPrefix(rank.getPrefix());
            team.setSuffix(rank.getPrefix());
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
            Rank rank = new Rank("default", "", "", scoreboard.registerNewTeam("a"));
            create(rank);
        }
    }
    public void saveConfiguration() {
        try {
            configuration.save(file);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
    public void setPrimaryRank(Rank rank) {
        primaryRank = rank;
        configuration.set("primary", rank.getName());
        saveConfiguration();
    }
    public void create(Rank rank) {
        ranks.add(rank);
        Team team = rank.getTeam();
        team.setPrefix(rank.getPrefix());
        team.setSuffix(rank.getSuffix());
        String name = rank.getName();
        configuration.set(name + ".prefix", rank.getPrefix());
        configuration.set(name + ".suffix", rank.getSuffix());
        configuration.set(name + ".team", rank.getTeam().getName());
        List<String> permissions = new ArrayList<>();
        for (Map.Entry<String, Boolean> set : rank.getPermissions().entrySet()) {
            permissions.add(set.getKey() + "," + set.getValue());
        }
        configuration.set(name + ".permissions", permissions);
        saveConfiguration();
    }
    public void updatePrefix(Rank rank) {
        configuration.set(rank.getName() + ".prefix", rank.getPrefix());
        saveConfiguration();
    }
    public void updateSuffix(Rank rank) {
        configuration.set(rank.getName() + ".suffix", rank.getSuffix());
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
    public void updateTeam(Rank rank) {
        Team team = rank.getTeam();
        configuration.set(rank.getName() + ".team", team.getName());
        plugin.getUserManager().updateMany(rank);
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
