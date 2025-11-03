package me.chengzhify.woolwarsutilities.levelsystem.commands;

import me.chengzhify.woolwarsutilities.levelsystem.storage.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.storage.MySQLManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ResetSubCommand extends SubCommand {
    private final String PREFIX = ChatColor.YELLOW + "[WoolWars Utilities] " + ChatColor.GRAY;
    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "重置玩家等级信息";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "用法: /wwl reset <玩家>");
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

            data.reset();
            LevelManager.asyncSavePlayer(targetUUID, realName);
            sender.sendMessage(PREFIX + ChatColor.YELLOW + "已重置玩家 " + realName + " 的等级数据。");
        });

        return true;
    }
}
