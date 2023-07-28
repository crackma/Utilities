package me.crackma.utilities;

import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import me.crackma.utilities.commands.DiscordCommand;
import me.crackma.utilities.commands.RulesCommand;
import me.crackma.utilities.commands.SpawnCommand;
import me.crackma.utilities.commands.VanishCommand;
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

public final class UtilitiesPlugin extends JavaPlugin {
    @Getter
    private GuiManager guiManager;
    @Getter
    private RankManager rankManager;
    @Getter
    private UserManager userManager;
    @Getter
    private UserDatabase userDatabase;
    @Getter
    private Utils utils;
    @Override
    public void onEnable() {
        getDataFolder().mkdirs();
        saveDefaultConfig();
        guiManager = new GuiManager();
        rankManager = new RankManager(this);
        userManager = new UserManager(this);
        userDatabase = new UserDatabase(this);
        utils = new Utils();
        new DiscordCommand(this);
        new RulesCommand(this);
        new SpawnCommand(this);
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
