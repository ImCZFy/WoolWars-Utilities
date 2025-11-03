package me.chengzhify.woolwarsutilities.levelsystem.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class LevelCommand implements CommandExecutor {

    private final List<SubCommand> subCommands = new ArrayList<>();

    public LevelCommand() {
        subCommands.add(new GetSubCommand());
        subCommands.add(new SetSubCommand());
        subCommands.add(new AddSubCommand());
        subCommands.add(new ResetSubCommand());
        subCommands.add(new IconSubCommand());
        subCommands.add(new HelpSubCommand());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "使用 /" + label + " help 查看帮助");
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
