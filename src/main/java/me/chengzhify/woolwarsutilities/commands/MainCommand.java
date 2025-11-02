package me.chengzhify.woolwarsutilities.commands;

import dev.pixelstudios.woolwars.WoolWars;
import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import me.chengzhify.woolwarsutilities.levelsystem.LevelFormatter;
import me.chengzhify.woolwarsutilities.levelsystem.MySQLManager;
import me.chengzhify.woolwarsutilities.levelsystem.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class MainCommand implements CommandExecutor {
    private final String PREFIX = ChatColor.YELLOW + "[WoolWars Utilities] " + ChatColor.GRAY;
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        // 权限检查
        if (!sender.hasPermission("woolwarsutilities.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限执行该命令!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + "使用 &a'/" + command.getName() + " help' &7获取帮助."));
            return true;
        }
        String sub = args[0];
        if (sub.equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&e&l羊毛战争&6Utilities"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7by ChengZhiFy"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "----------------"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/" + command.getName() + " help &7查看本菜单"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/" + command.getName() + " reload &7重载插件"));
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&a/wwl help &7查看等级系统菜单"));
            return true;
        }
        WoolWarsUtilities plugin = WoolWarsUtilities.getInstance();
        if (sub.equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + ChatColor.GRAY + "正在重新加载 &6WoolWars-Utilities &7配置和数据库..."));

            plugin.reloadConfig();
            LevelFormatter.loadColors(plugin);

            MySQLManager.disconnect();
            if (plugin.getConfig().getBoolean("mysql.enable")) {
                String host = plugin.getConfig().getString("mysql.host", "localhost");
                int port = plugin.getConfig().getInt("mysql.port", 3306);
                String database = plugin.getConfig().getString("mysql.database", "woolwars_utilities");
                String user = plugin.getConfig().getString("mysql.user", "root");
                String password = plugin.getConfig().getString("mysql.password", "");
                MySQLManager.connect(plugin, host, port, database, user, password);
            }

            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
                Bukkit.getOnlinePlayers().forEach(player -> {
                    LevelManager.asyncLoadPlayer(player);
                    Bukkit.getScheduler().runTask(plugin, () -> WoolWars.get().reload());
                });
            });
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + ChatColor.GRAY + "&6WoolWars-Utilities &7重载完成!"));
        }
        return true;
    }
}