package me.crackma.utilities.rank;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;

public class Rank {
	@Getter
    private String name;
    @Setter
    private String prefix, suffix;
    @Getter
    @Setter
    private ChatColor nametagColor;
    @Getter
    @Setter
    private Team team;
    @Getter
    private HashMap<String, Boolean> permissions = new HashMap<>();
    public Rank(String name, String prefix, String suffix, ChatColor nametagColor, Team team) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
        this.nametagColor = nametagColor;
        this.team = team;
        team.setPrefix(prefix);
        team.setSuffix(suffix);
        team.setColor(nametagColor);
    }
    public void setPermission(String permission, boolean value) {
        permissions.put(permission, value);
    }
    public String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', prefix);
    }
    public String getSuffix() {
        return ChatColor.translateAlternateColorCodes('&', suffix);
    }
}
