package me.crackma.utilities;

import lombok.Getter;
import me.crackma.utilities.commands.DiscordCommand;
import me.crackma.utilities.commands.RulesCommand;
import me.crackma.utilities.commands.SpawnCommand;
import me.crackma.utilities.commands.TestCommand;
import me.crackma.utilities.gui.GuiListener;
import me.crackma.utilities.gui.GuiManager;
import me.crackma.utilities.punishments.RevokeCommand;
import me.crackma.utilities.rank.RankCommand;
import me.crackma.utilities.rank.RankManager;
import me.crackma.utilities.user.UserDatabase;
import me.crackma.utilities.user.UserListener;
import me.crackma.utilities.user.UserManager;
import me.crackma.utilities.punishments.PunishCommand;
import me.crackma.utilities.user.info.InfoCommand;
import me.crackma.utilities.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

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
        new DiscordCommand(this);
        guiManager = new GuiManager();
        rankManager = new RankManager(this);
        userManager = new UserManager(this);
        userDatabase = new UserDatabase(this);
        utils = new Utils();
        new RulesCommand(this);
        new SpawnCommand(this);
        new TestCommand(this);
        new GuiListener(this);
        new PunishCommand(this);
        new RevokeCommand(this);
        new RankCommand(this);
        new InfoCommand(this);
        new UserListener(this);
    }
    @Override
    public void onDisable() {
        // TODO: add onDisable
    }
}
