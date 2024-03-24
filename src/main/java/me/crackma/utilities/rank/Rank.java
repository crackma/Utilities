package me.crackma.utilities.rank;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;

import lombok.Getter;
import lombok.Setter;

public class Rank {
  @Getter
  private String name;
  @Setter
  private String prefix, suffix;
  @Getter
  @Setter
  private ChatColor nameColor;
  @Getter
  @Setter
  private Team team;
  @Getter
  private HashMap<String, Boolean> permissions = new HashMap<>();
  public Rank(String name, String prefix, String suffix, ChatColor nameColor, Team team) {
    this.name = name;
    this.prefix = prefix;
    this.suffix = suffix;
    this.nameColor = nameColor;
    this.team = team;
    team.setPrefix(prefix);
    team.setSuffix(suffix);
    team.setColor(nameColor);
  }
  public void setPermission(String permission, boolean value) {
    permissions.put(permission, value);
  }
  public void removePermission(String permission) {
    permissions.remove(permission);
  }
  public String getPurePrefix() {
    return prefix.replace("§", "&");
  }
  public String getPrefix() {
    return ChatColor.translateAlternateColorCodes('&', prefix);
  }
  public String getPureSuffix() {
    return suffix.replace("§", "&");
  }
  public String getSuffix() {
    return ChatColor.translateAlternateColorCodes('&', suffix);
  }
}
