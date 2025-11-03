package me.chengzhify.woolwarsutilities.levelsystem.commands;

import me.chengzhify.woolwarsutilities.levelsystem.storage.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.storage.MySQLManager;
import me.chengzhify.woolwarsutilities.levelsystem.display.LevelFormatter;
import me.chengzhify.woolwarsutilities.levelsystem.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class GetSubCommand extends SubCommand {
    private final String PREFIX = ChatColor.YELLOW + "[WoolWars Utilities] " + ChatColor.GRAY;
    @Override
    public String getName() {
        return "get";
    }

    @Override
    public String getDescription() {
        return "获取玩家等级信息";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "用法: /wwl get <玩家>");
            return true;
        }

        String targetName = args[1];
        String targetUUIDStr = MySQLManager.getPlayerUUID(targetName);
        if (targetUUIDStr == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "玩家未找到: " + targetName);
            return true;
        }

        String realName = MySQLManager.getPlayerName(targetUUIDStr);
        if (realName == null) {
            sender.sendMessage(PREFIX + ChatColor.RED + "无法获取玩家名称！");
            return true;
        }

        UUID targetUUID = UUID.fromString(targetUUIDStr);

        LevelManager.asyncLoadPlayer(targetUUID, realName, data -> {
            if (data == null) {
                sender.sendMessage(PREFIX + ChatColor.RED + "玩家数据加载失败！");
                return;
            }
                String level = LevelFormatter.getColoredLevel(data.getLevel(), data.getIcon());
                int exp = data.getExp();
                int expToNext = data.getExpToNextLevel(data.getLevel());
                String progress = LevelManager.getProgressBar(exp, expToNext, 10);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "玩家 &a" + realName + " &7的羊毛战争等级信息:"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "等级: " + level));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "进度: &b" + exp + "&7/&a" + expToNext));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "  " + progress));
        });
        return true;
    }
}
