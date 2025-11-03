package me.chengzhify.woolwarsutilities.levelsystem.commands;

import me.chengzhify.woolwarsutilities.levelsystem.storage.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.storage.MySQLManager;
import me.chengzhify.woolwarsutilities.levelsystem.display.LevelFormatter;
import me.chengzhify.woolwarsutilities.levelsystem.commands.SubCommand;
import dev.pixelstudios.woolwars.WoolWars;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

import static java.lang.Integer.parseInt;

public class SetSubCommand extends SubCommand {
    private final String PREFIX = ChatColor.YELLOW + "[WoolWars Utilities] " + ChatColor.GRAY;
    @Override
    public String getName() {
        return "set";
    }

    @Override
    public String getDescription() {
        return "设置玩家等级或经验";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(PREFIX + ChatColor.RED + "用法: /wwl set <玩家> <level | exp> <数量>");
            return true;
        }

        String targetName = args[1];
        String type = args[2];
        int amount;

        try {
            amount = parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(PREFIX + ChatColor.RED + "请输入正确的数字。");
            return true;
        }

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
            if (type.equalsIgnoreCase("level")) {
                data.setLevel(amount);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "玩家 &a" + realName + "&7 的等级已设置为 " + LevelFormatter.getColoredLevel(data.getLevel(), data.getIcon())));
            } else if (type.equalsIgnoreCase("exp") || type.equalsIgnoreCase("xp")) {
                data.setExp(amount);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "玩家 &a" + realName + "&7 的经验已设置为 " + data.getExp()));
            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + ChatColor.RED + "无效的数据类型! 可用类型: level, exp"));
                return;
            }
            LevelManager.asyncSavePlayer(targetUUID, realName, () -> WoolWars.get().reload());
        });

        return true;
    }
}
