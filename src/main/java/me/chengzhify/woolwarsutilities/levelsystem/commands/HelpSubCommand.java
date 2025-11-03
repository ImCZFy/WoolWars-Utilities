package me.chengzhify.woolwarsutilities.levelsystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class HelpSubCommand extends SubCommand {
    private final String PREFIX = ChatColor.YELLOW + "[WoolWars Utilities] " + ChatColor.GRAY;

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getDescription() {
        return "显示等级系统命令帮助";
    }

    @Override
    public boolean requiresAdmin() {
        return false; // help 可以让任何人查看
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l羊毛战争 &6等级组件"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7by ChengZhiFy"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "----------------"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/wwl get <玩家> &7获取玩家等级"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/wwl set <玩家> <level|exp> <数量> &7设置等级或经验"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/wwl add <玩家> <level|exp> <数量> &7增加等级或经验"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/wwl reset <玩家> &7重置玩家等级信息"));
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/wwl icon &7修改等级图标"));
        return true;
    }
}
