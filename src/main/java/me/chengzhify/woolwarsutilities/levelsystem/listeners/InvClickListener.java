package me.chengzhify.woolwarsutilities.levelsystem.listeners;

import dev.pixelstudios.woolwars.WoolWars;
import me.chengzhify.woolwarsutilities.levelsystem.storage.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.storage.MySQLManager;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class InvClickListener implements Listener {

    @EventHandler
    public void onIconClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player player)) return;
        String title = PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        if (!title.equalsIgnoreCase("图标选择")) return;
        e.setCancelled(true);

        ItemStack clicked = e.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        ItemMeta meta = clicked.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null || lore.get(lore.size() - 1).contains("未解锁")) return;

        String iconName = ChatColor.stripColor(meta.getDisplayName());
        LevelManager.getData(e.getWhoClicked().getUniqueId()).setIcon(iconName);
        player.sendMessage(ChatColor.GREEN + "已选择图标: " + iconName);
        LevelManager.asyncSavePlayer(e.getWhoClicked().getUniqueId(), e.getWhoClicked().getName(), () -> WoolWars.get().reload());
        player.closeInventory();
    }

}
