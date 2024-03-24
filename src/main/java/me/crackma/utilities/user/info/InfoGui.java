package me.crackma.utilities.user.info;

import me.crackma.utilities.UtilitiesPlugin;
import me.crackma.utilities.gui.Gui;
import me.crackma.utilities.gui.GuiButton;
import me.crackma.utilities.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class InfoGui extends Gui {
  private UtilitiesPlugin plugin;
  private User user;
  public InfoGui(UtilitiesPlugin plugin, User user) {
    super();
    this.plugin = plugin;
    this.user = user;
  }
  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 27);
  }
  @Override
  public void decorate() {
    ItemStack info = new ItemStack(Material.WITHER_SKELETON_SKULL);
    ItemMeta infoMeta = info.getItemMeta();
    infoMeta.setDisplayName("§bInfo");
    infoMeta.setLore(Arrays.asList(
        "§fUniqueId: §b" + user.getUniqueId().toString(),
        "§fRank: §b" + user.getRank().getName()));
    info.setItemMeta(infoMeta);
    addButton(12, new GuiButton().creator(unused -> info));
    ItemStack punishments = new ItemStack(Material.FIRE_CHARGE);
    ItemMeta punishmentsMeta = punishments.getItemMeta();
    punishmentsMeta.setDisplayName("§bPunishments");
    punishmentsMeta.setLore(Arrays.asList("§fClick to view"));
    punishments.setItemMeta(punishmentsMeta);
    addButton(14, new GuiButton().creator(unused -> punishments).
        leftConsumer(event -> plugin.getGuiManager().openGUI(new PunishmentsGui(user), event.getWhoClicked())).
        rightConsumer(event -> plugin.getGuiManager().openGUI(new PunishmentsGui(user), event.getWhoClicked())));
    super.decorate();
  }
}
