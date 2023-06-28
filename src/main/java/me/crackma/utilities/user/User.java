package me.crackma.utilities.user;

import lombok.Getter;
import lombok.Setter;
import me.crackma.utilities.punishments.Punishment;
import me.crackma.utilities.punishments.PunishmentType;
import me.crackma.utilities.rank.Rank;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class User {
    @Getter
    private final UUID uniqueId;
    @Getter
    @Setter
    private Player player;
    @Getter
    @Setter
    private Rank rank;
    @Getter
    private Set<Punishment> punishments = new HashSet<>();
    @Getter
    @Setter
    private boolean vanished;
    @Getter
    @Setter
    private boolean modMode;
    public User(UUID uniqueId, Rank rank, String punishments) {
        this.uniqueId = uniqueId;
        this.rank = rank;
        //TODO: add vanish and moderator mode
        vanished = false;
        modMode = false;
        if (punishments == null || punishments.trim().isEmpty()) return;
        String[] punishmentsArray = punishments.split(";");
        for (String punishment : punishmentsArray) {
            this.punishments.add(new Punishment(punishment));
        }
    }
    public String getDisplayName() {
        return ChatColor.translateAlternateColorCodes('&', rank.getPrefix() + player.getName() + rank.getSuffix());
    }
    public TextComponent getInfoTextComponent() {
        TextComponent textComponent = new TextComponent(getDisplayName());
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                new ComponentBuilder("Name: " + player.getName() + "\nRank: " + rank.getName()).create()));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/info " + player.getName()));
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
