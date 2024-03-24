package me.crackma.utilities;

import me.crackma.utilities.commands.*;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.Setter;
import me.crackma.utilities.gui.GuiListener;
import me.crackma.utilities.gui.GuiManager;
import me.crackma.utilities.punishments.PunishCommand;
import me.crackma.utilities.punishments.RevokeCommand;
import me.crackma.utilities.rank.RankCommand;
import me.crackma.utilities.rank.RankManager;
import me.crackma.utilities.user.UserDatabase;
import me.crackma.utilities.user.UserListener;
import me.crackma.utilities.user.UserManager;
import me.crackma.utilities.user.info.InfoCommand;
import me.crackma.utilities.utils.Utils;

@Getter
public final class UtilitiesPlugin extends JavaPlugin {
    private GuiManager guiManager;
    private RankManager rankManager;
    private UserManager userManager;
    private UserDatabase userDatabase;
    private Utils utils;
    @Setter
    private boolean chatMuted;
    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        saveDefaultConfig();
        guiManager = new GuiManager();
        rankManager = new RankManager(this);
        userManager = new UserManager(this);
        userDatabase = new UserDatabase(this);
        utils = new Utils();
        chatMuted = false;
        new DiscordCommand(this);
        new MessageCommand(this);
        new MuteChatCommand(this);
        new RenameCommand(this);
        new RulesCommand(this);
        new SpawnCommand(this);
        new TestCommand(this);
        new VanishCommand(this);
        new GuiListener(this);
        new PunishCommand(this);
        new RevokeCommand(this);
        new RankCommand(this);
        new InfoCommand(this);
        new UserListener(this);
    }
    @Override
    public void onDisable() {
        rankManager.unload();
    }
}
