package me.chengzhify.woolwarsutilities.commands;


import me.chengzhify.woolwarsutilities.commands.*;
import me.chengzhify.woolwarsutilities.commands.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MainCommand implements CommandExecutor {
    private final String PREFIX = ChatColor.YELLOW + "[WoolWars Utilities] " + ChatColor.GRAY;

    private final List<SubCommand> subCommands = new ArrayList<>();

    public MainCommand() {
        subCommands.add(new HelpSubCommand());
        subCommands.add(new ReloadSubCommand());
    }
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
        for (SubCommand subCmd : subCommands) {
            if (subCmd.getName().equalsIgnoreCase(sub)) {
                if (subCmd.requiresAdmin() && !sender.hasPermission("woolwarsutilities.admin")) {
                    sender.sendMessage(ChatColor.RED + "你没有权限执行此命令！");
                    return true;
                }
                return subCmd.execute(sender, args);
            }
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c未知子命令: " + sub + "，输入 /" + label + " help 查看帮助"));
        return true;
    }
}