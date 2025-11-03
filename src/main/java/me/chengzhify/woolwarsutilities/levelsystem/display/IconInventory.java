package me.chengzhify.woolwarsutilities.levelsystem.display;

import me.chengzhify.woolwarsutilities.levelsystem.storage.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.storage.MySQLManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class IconInventory {

    private Player player;
    private int level;

    private static final int MAX_ICON = 10; // 100~1000 级每百级解锁 10 个图标
    private static final String[] ICONS = {
            "❤", "✵", "✤", "✿", "✙",
            "♛", "❄", "☃", "♫", "✺"
    };

    public IconInventory(Player player, int level) {
        this.player = player;
        this.level = level;
    }

    public Inventory createInventory() {
        Inventory inv = Bukkit.createInventory(player, 54, "图标选择");

        // 第一个格子显示玩家等级信息
        ItemStack playerHead = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta headMeta = (SkullMeta) playerHead.getItemMeta();
        if (headMeta != null) {
            headMeta.setDisplayName(ChatColor.AQUA + player.getName() + " 的等级信息");
            headMeta.setOwningPlayer(player);
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "等级: " + LevelFormatter.getColoredLevel(level, LevelManager.getData(player).getIcon()));
            lore.add(ChatColor.GRAY + "当前经验: " + LevelManager.getData(player).getExp());
            headMeta.setLore(lore);

            playerHead.setItemMeta(headMeta);
        }
        inv.setItem(0, playerHead);

        int rowLength = 9;
        int leftPadding = 2;
        int rightPadding = 2;
        int iconsPerRow = rowLength - leftPadding - rightPadding;
        int currentRow = 2;
        int slotIndex = currentRow * rowLength + leftPadding; // 20

        String currentIcon = LevelManager.getData(player).getIcon();

        for (int i = 0; i < ICONS.length; i++) {
            ItemStack iconItem = new ItemStack(Material.PAPER);
            ItemMeta meta = iconItem.getItemMeta();
            if (meta == null) continue;

            meta.setDisplayName(ChatColor.GREEN + ICONS[i]);
            List<String> lore = new ArrayList<>();
            lore.add("");
            lore.add("");
            lore.add(ChatColor.GRAY + "解锁等级: " + LevelFormatter.getColoredLevel(((i + 1) * 100), ICONS[i]));
            if (level >= (i + 1) * 100) {
                lore.add(ChatColor.GREEN + "已解锁" + (ICONS[i].equals(currentIcon) ? " (当前)" : ""));
            } else {
                lore.add(ChatColor.RED + "未解锁");
            }
            meta.setLore(lore);
            iconItem.setItemMeta(meta);

            inv.setItem(slotIndex, iconItem);

            int posInRow = (slotIndex % rowLength) - leftPadding;
            if (posInRow >= iconsPerRow - 1) {
                currentRow++;
                slotIndex = currentRow * rowLength + leftPadding;
            } else {
                slotIndex++;
            }
        }

        return inv;
    }


    public void openInventory() {
        player.openInventory(createInventory());
    }
}