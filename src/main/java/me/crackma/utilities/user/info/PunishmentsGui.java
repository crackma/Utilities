package me.crackma.utilities.user.info;

import me.crackma.utilities.gui.Gui;
import me.crackma.utilities.gui.GuiButton;
import me.crackma.utilities.punishments.Punishment;
import me.crackma.utilities.punishments.PunishmentType;
import me.crackma.utilities.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class PunishmentsGui extends Gui {
  private User user;
  public PunishmentsGui(User user) {
    super();
    this.user = user;
  }
  @Override
  public Inventory createInventory() {
    return Bukkit.createInventory(null, 27);
  }
  public void addPunishment(int inventorySlot, Punishment punishment) {
    ItemStack itemStack;
    if (punishment.getType() == PunishmentType.BAN) {
      itemStack = new ItemStack(Material.STONE_AXE);
    } else {
      itemStack = new ItemStack(Material.BOOK);
    }
    ItemMeta itemMeta = itemStack.getItemMeta();
    itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
    itemMeta.setDisplayName("§b§o" + punishment.getType().toString());
    itemMeta.setLore(Arrays.asList(
        "§fIssuer: §b" + punishment.getIssuer(),
        "§fReason: §b" + punishment.getReason(),
        "§fIssue date: §b" + punishment.getIssueDate(),
        "§fExpiry date: §b" + punishment.getExpiryDate(),
        "§fRevoked: §b" + punishment.isRevoked()));
    itemStack.setItemMeta(itemMeta);
    this.addButton(inventorySlot, new GuiButton().creator(unused -> itemStack));
  }
  @Override
  public void decorate() {
    int inventorySlot = 0;
    for (Punishment punishment : user.getPunishments()) {
      addPunishment(inventorySlot, punishment);
      inventorySlot++;
    }
    super.decorate();
  }
}
