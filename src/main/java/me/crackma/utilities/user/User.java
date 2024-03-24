package me.crackma.utilities.user;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;

import lombok.Getter;
import lombok.Setter;
import me.crackma.utilities.punishments.Punishment;
import me.crackma.utilities.punishments.PunishmentType;
import me.crackma.utilities.rank.Rank;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.permissions.PermissionAttachment;

@Getter
public class User {
    private final UUID uniqueId;
    @Setter
    private OfflinePlayer offlinePlayer;
    @Setter
    private Rank rank;
    private Set<Punishment> punishments = new HashSet<>();
    @Setter
    private PermissionAttachment permissionAttachment;
    @Setter
    private boolean vanished;
    @Setter
    private boolean modMode;
    public User(UUID uniqueId, Rank rank, String punishments) {
        this.uniqueId = uniqueId;
        this.rank = rank;
        //TODO: add moderator mode
        vanished = false;
        modMode = false;
        if (punishments == null || punishments.trim().isEmpty()) return;
        String[] punishmentsArray = punishments.split(";");
        for (String punishment : punishmentsArray) {
            this.punishments.add(new Punishment(punishment));
        }
    }
    public String getDisplayName() {
        return ChatColor.translateAlternateColorCodes('&', rank.getPrefix() + offlinePlayer.getName() + rank.getSuffix());
    }
    public TextComponent getInfoTextComponent() {
        TextComponent textComponent = new TextComponent(getDisplayName());
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Name: " + offlinePlayer.getName() + "\nRank: " + rank.getName()).create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/utilities:info " + offlinePlayer.getName()));
        return textComponent;
    }
    public void addPunishment(Punishment punishment) {
        punishments.add(punishment);
    }
    public Punishment findActiveBan() {
        for (Punishment punishment : punishments) {
            if (punishment.getType() != PunishmentType.BAN || punishment.hasExpired() || punishment.isRevoked()) continue;
            return punishment;
        }
        return null;
    }
    public Punishment findActiveMute() {
        for (Punishment punishment : punishments) {
            if (punishment.getType() != PunishmentType.MUTE || punishment.hasExpired() || punishment.isRevoked()) continue;
            return punishment;
        }
        return null;
    }
    public String punishmentsToString() {
        StringBuilder stringBuilder = new StringBuilder();
        punishments.forEach(punishment -> stringBuilder.append(punishment.toString() + ";"));
        return stringBuilder.toString();
    }
}
