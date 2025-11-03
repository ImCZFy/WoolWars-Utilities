package me.chengzhify.woolwarsutilities.commands;

import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
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
        return "查看帮助页";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l羊毛战争&6Utilities"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7by ChengZhiFy"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "----------------"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/wwu help &7查看本菜单"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/wwu reload &7重载插件"));
            if (WoolWarsUtilities.getInstance().getConfig().getBoolean("mysql.enable")) sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/wwl help &7查看等级系统菜单"));
            return true;
    }
}
