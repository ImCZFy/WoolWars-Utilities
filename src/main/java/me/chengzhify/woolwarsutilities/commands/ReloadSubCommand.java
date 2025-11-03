package me.chengzhify.woolwarsutilities.commands;

import dev.pixelstudios.woolwars.WoolWars;
import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import me.chengzhify.woolwarsutilities.levelsystem.display.LevelFormatter;
import me.chengzhify.woolwarsutilities.levelsystem.storage.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.storage.MySQLManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ReloadSubCommand extends SubCommand {
    private final String PREFIX = ChatColor.YELLOW + "[WoolWars Utilities] " + ChatColor.GRAY;
    WoolWarsUtilities plugin = WoolWarsUtilities.getInstance();
    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "重载配置";
    }

    @Override
    public boolean execute(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', PREFIX + ChatColor.GRAY + "正在重新加载 &6WoolWars-Utilities &7配置和数据库..."));
        Bukkit.getScheduler().cancelTasks(plugin);
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
        return true;
    }
}
