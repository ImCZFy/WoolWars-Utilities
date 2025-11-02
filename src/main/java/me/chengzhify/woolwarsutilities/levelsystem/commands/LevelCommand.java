package me.chengzhify.woolwarsutilities.levelsystem.commands;

import me.chengzhify.woolwarsutilities.levelsystem.LevelData;
import me.chengzhify.woolwarsutilities.levelsystem.LevelFormatter;
import me.chengzhify.woolwarsutilities.levelsystem.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.MySQLManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import static java.lang.Integer.parseInt;

public class LevelCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l羊毛战争&6等级组件"));
            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7使用 &a'/" + command.getName() + " help' &7获取帮助."));
            return true;
        }
        if  (strings.length == 1) {
            String arg1 = strings[0];
            if (arg1.equalsIgnoreCase("help")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l羊毛战争&6等级组件"));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7by ChengZhiFy"));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "----------------"));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a'/" + command.getName() + "get <ID> &7获取某个玩家的等级信息"));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a'/" + command.getName() + "set <ID> &7<Type> <Amount> &7修改某个玩家的等级数值"));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a'/" + command.getName() + "add <ID> &7<Type> <Amount> &7增加某个玩家的等级数值"));
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a'/" + command.getName() + "reset <ID> &7重置某个玩家的等级信息"));
                return true;
            } else if (arg1.equalsIgnoreCase("get")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "get <ID>"));
                return true;
            } else if (arg1.equalsIgnoreCase("set")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "set <ID> <Type> <Amount>"));
                return true;
            } else if (arg1.equalsIgnoreCase("add")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "add <ID> <Type> <Amount>"));
                return true;
            } else if (arg1.equalsIgnoreCase("reset")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "reset <ID>"));
                return true;
            }


        }
        if  (strings.length == 2) {
            String arg1 = strings[0];
            String player = strings[1];
            if (arg1.equalsIgnoreCase("get")) {
                if (MySQLManager.playerExists(player) || MySQLManager.playerExists(UUID.fromString(player))) {
                    LevelManager.asyncLoadPlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                    LevelData playerData = LevelManager.getData(MySQLManager.getPlayerUUID(player));
                    String level = LevelFormatter.getColoredLevel(playerData.getLevel());
                    int exp = playerData.getExp();
                    int exptonext = playerData.getExpToNextLevel(playerData.getLevel());
                    String progressbar = LevelManager.getProgressBar(exp, exptonext, 10);
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7玩家 " +MySQLManager.getPlayerName(player) + "的羊毛战争等级信息"));
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7等级: " + level));
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7进度: &b" + exp + "&7/&a" + exptonext));
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "  " + progressbar));
                    return true;
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c该玩家不存在于数据库中, 请检查信息是否有误!"));
                    return true;
                }
            } else if (arg1.equalsIgnoreCase("set")) {
                if (MySQLManager.playerExists(player) || MySQLManager.playerExists(UUID.fromString(player))) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "set <ID> <Type> <Amount>"));
                    return true;
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c该玩家不存在于数据库中, 请检查信息是否有误!"));
                    return true;
                }
            } else if (arg1.equalsIgnoreCase("add")) {
                if (MySQLManager.playerExists(player) || MySQLManager.playerExists(UUID.fromString(player))) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "add <ID> <Type> <Amount>"));
                    return true;
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c该玩家不存在于数据库中, 请检查信息是否有误!"));
                    return true;
                }
            } else if (arg1.equalsIgnoreCase("reset")) {
                if (MySQLManager.playerExists(player) || MySQLManager.playerExists(UUID.fromString(player))) {
                    LevelManager.asyncLoadPlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                    LevelData playerData = LevelManager.getData(MySQLManager.getPlayerUUID(player));
                    playerData.reset();
                    LevelManager.asyncSavePlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                    return true;
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c该玩家不存在于数据库中, 请检查信息是否有误!"));
                    return true;
                }
            }
        }
        if (strings.length == 3) {
            String arg1 = strings[0];
            String player = strings[1];
            String type = strings[2];
            if (arg1.equalsIgnoreCase("set")) {
                if (MySQLManager.playerExists(player) || MySQLManager.playerExists(UUID.fromString(player))) {
                    if (type.equalsIgnoreCase("level")) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "set <ID> " + type + "<Amount>"));
                        return true;
                    } else if (type.equalsIgnoreCase("exp") || type.equalsIgnoreCase("xp")) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "set <ID> " + type + "<Amount>"));
                        return true;
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c无效的数据类型! 可用数据类型: level, exp"));
                        return true;
                    }
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c该玩家不存在于数据库中, 请检查信息是否有误!"));
                    return true;
                }
            } else if (arg1.equalsIgnoreCase("add")) {
                if (MySQLManager.playerExists(player) || MySQLManager.playerExists(UUID.fromString(player))) {
                    if (type.equalsIgnoreCase("level")) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "add <ID> " + type + "<Amount>"));
                        return true;
                    } else if (type.equalsIgnoreCase("exp") || type.equalsIgnoreCase("xp")) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c用法: " + "/" + command.getName() + "add <ID> " + type + "<Amount>"));
                        return true;
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c无效的数据类型! 可用数据类型: level, exp"));
                        return true;
                    }
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c该玩家不存在于数据库中, 请检查信息是否有误!"));
                    return true;
                }
            }
        }
        if (strings.length == 4) {
            String arg1 = strings[0];
            String player = strings[1];
            String type = strings[2];
            int amount = parseInt(strings[3]);
            if (arg1.equalsIgnoreCase("set")) {
                if (MySQLManager.playerExists(player) || MySQLManager.playerExists(UUID.fromString(player))) {
                    if (type.equalsIgnoreCase("level")) {
                        LevelManager.asyncLoadPlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                        LevelData playerData = LevelManager.getData(MySQLManager.getPlayerUUID(player));
                        playerData.setLevel(amount);
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7玩家 " +MySQLManager.getPlayerName(player) + "的羊毛战争等级现在是 " + LevelFormatter.getColoredLevel(playerData.getLevel())));
                        LevelManager.asyncSavePlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                        return true;
                    } else if (type.equalsIgnoreCase("exp") || type.equalsIgnoreCase("xp")) {
                        LevelManager.asyncLoadPlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                        LevelData playerData = LevelManager.getData(MySQLManager.getPlayerUUID(player));
                        playerData.setExp(amount);
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7玩家 " +MySQLManager.getPlayerName(player) + "的羊毛战争经验现在是 " + playerData.getExp() + ", 等级为 " + LevelFormatter.getColoredLevel(playerData.getLevel())));
                        LevelManager.asyncSavePlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                        return true;
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c无效的数据类型! 可用数据类型: level, exp"));
                        return true;
                    }
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c该玩家不存在于数据库中, 请检查信息是否有误!"));
                    return true;
                }
            } else if (arg1.equalsIgnoreCase("add")) {
                if (MySQLManager.playerExists(player) || MySQLManager.playerExists(UUID.fromString(player))) {
                    if (type.equalsIgnoreCase("level")) {
                        LevelManager.asyncLoadPlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                        LevelData playerData = LevelManager.getData(MySQLManager.getPlayerUUID(player));
                        playerData.addLevel(amount);
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7玩家 " +MySQLManager.getPlayerName(player) + "的羊毛战争等级现在是 " + LevelFormatter.getColoredLevel(playerData.getLevel())));
                        LevelManager.asyncSavePlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                        return true;
                    } else if (type.equalsIgnoreCase("exp") || type.equalsIgnoreCase("xp")) {
                        LevelManager.asyncLoadPlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                        LevelData playerData = LevelManager.getData(MySQLManager.getPlayerUUID(player));
                        playerData.addExp(amount);
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7玩家 " +MySQLManager.getPlayerName(player) + "的羊毛战争经验现在是 " + playerData.getExp() + ", 等级为 " + LevelFormatter.getColoredLevel(playerData.getLevel())));
                        LevelManager.asyncSavePlayer(MySQLManager.getPlayerUUID(player), MySQLManager.getPlayerName(player));
                        return true;
                    } else {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c无效的数据类型! 可用数据类型: level, exp"));
                        return true;
                    }
                } else {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c该玩家不存在于数据库中, 请检查信息是否有误!"));
                    return true;
                }
            }
        }
        return false;
    }
}