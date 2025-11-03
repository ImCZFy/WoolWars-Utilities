package me.chengzhify.woolwarsutilities.levelsystem.commands;

import me.chengzhify.woolwarsutilities.levelsystem.display.IconInventory;
import me.chengzhify.woolwarsutilities.levelsystem.storage.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IconSubCommand extends SubCommand {
    private final String PREFIX = ChatColor.YELLOW + "[WoolWars Utilities] " + ChatColor.GRAY;
    @Override
    public String getName() {
        return "icon";
    }

    @Override
    public String getDescription() {
        return "打开等级图标选择器";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(PREFIX + ChatColor.RED + "此命令只能由玩家执行！");
            return true;
        }
        Player player = (Player) sender;
        IconInventory iconSelector = new IconInventory(player, LevelManager.getData(player).getLevel());
        iconSelector.openInventory();

        return true;
    }
}
