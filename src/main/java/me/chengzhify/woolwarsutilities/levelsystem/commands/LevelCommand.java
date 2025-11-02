package me.chengzhify.woolwarsutilities.levelsystem.commands;

import dev.pixelstudios.woolwars.WoolWars;
import me.chengzhify.woolwarsutilities.levelsystem.LevelData;
import me.chengzhify.woolwarsutilities.levelsystem.LevelFormatter;
import me.chengzhify.woolwarsutilities.levelsystem.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.MySQLManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static java.lang.Integer.parseInt;

public class LevelCommand implements CommandExecutor {

    private final String PREFIX = ChatColor.YELLOW + "[WoolWars Utilities] " + ChatColor.GRAY;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (!sender.hasPermission("woolwarsutilities.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用该命令!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "使用 &a'/" + command.getName() + " help' &7获取帮助."));
            return true;
        }

        String sub = args[0];

        if (sub.equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l羊毛战争 &6等级组件"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7by ChengZhiFy"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "----------------"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/" + command.getName() + " get <ID> &7获取玩家等级"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/" + command.getName() + " set <ID> <Type> <Amount> &7设置等级或经验"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/" + command.getName() + " add <ID> <Type> <Amount> &7增加等级或经验"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/" + command.getName() + " reset <ID> &7重置玩家等级信息"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(PREFIX + ChatColor.RED + "用法错误! 输入 /" + command.getName() + " help 查看帮助。");
            return true;
        }

        String targetName = args[1];
        String targetUUIDStr = MySQLManager.getPlayerUUID(targetName);
        if (targetUUIDStr == null) {
            sender.sendMessage("§c数据库中未找到玩家 " + targetName + "！");
            return true;
        }

        String realName = MySQLManager.getPlayerName(targetUUIDStr);
        if (realName == null) {
            sender.sendMessage("§c无法获取玩家名称！");
            return true;
        }

        UUID targetUUID = UUID.fromString(targetUUIDStr);

        if (sub.equalsIgnoreCase("get")) {
            LevelManager.asyncLoadPlayer(targetUUID, realName, data -> {
                if (data == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "&c玩家数据加载失败！"));
                    return;
                }

                String level = LevelFormatter.getColoredLevel(data.getLevel());
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

        // RESET 子命令
        if (sub.equalsIgnoreCase("reset")) {
            LevelManager.asyncLoadPlayer(targetUUID, realName, data -> {
                if (data == null) {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "&c玩家数据加载失败！"));
                    return;
                }
                data.reset();
                LevelManager.asyncSavePlayer(targetUUID, realName);
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "已重置玩家 &a" + realName + " &7的等级数据。"));
            });
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + ChatColor.RED + "用法: /" + command.getName() + " " + sub + " <ID> <Type> <Amount>"));
            return true;
        }

        String type = args[2];
        int amount;
        try {
            amount = parseInt(args[3]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + ChatColor.RED + "请输入正确的数字。"));
            return true;
        }

        LevelManager.asyncLoadPlayer(targetUUID, realName, data -> {
            if (data == null) {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "&c玩家数据加载失败！"));
                return;
            }

            if (sub.equalsIgnoreCase("set")) {
                if (type.equalsIgnoreCase("level")) {
                    data.setLevel(amount);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "玩家 &a" + realName + "&7 的等级已设置为 " + LevelFormatter.getColoredLevel(data.getLevel())));
                } else if (type.equalsIgnoreCase("exp") || type.equalsIgnoreCase("xp")) {
                    data.setExp(amount);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "玩家 &a" + realName + "&7 的经验已设置为 " + data.getExp()));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + ChatColor.RED + "无效的数据类型! 可用类型: level, exp"));
                    return;
                }
                LevelManager.asyncSavePlayer(targetUUID, realName);
                WoolWars.get().reload();
            } else if (sub.equalsIgnoreCase("add")) {
                if (type.equalsIgnoreCase("level")) {
                    data.addLevel(amount);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "玩家 &a" + realName + "&7 的等级现在为 " + LevelFormatter.getColoredLevel(data.getLevel())));
                } else if (type.equalsIgnoreCase("exp") || type.equalsIgnoreCase("xp")) {
                    data.addExp(amount);
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "玩家 &a" + realName + "&7 的经验现在为 " + data.getExp()));
                } else {
                    sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + ChatColor.RED + "无效的数据类型! 可用类型: level, exp"));
                    return;
                }
                LevelManager.asyncSavePlayer(targetUUID, realName, () -> {
                    WoolWars.get().reload();
                });

            } else {
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + ChatColor.RED + "未知子命令，请使用 /" + command.getName() + " help 查看帮助。"));
            }
        });

        return true;
    }
}
