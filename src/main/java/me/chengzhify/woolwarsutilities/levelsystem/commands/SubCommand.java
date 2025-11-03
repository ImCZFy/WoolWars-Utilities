package me.chengzhify.woolwarsutilities.levelsystem.commands;

import org.bukkit.command.CommandSender;

public abstract class SubCommand {

    public abstract String getName();

    public String[] getAliases() {
        return new String[0];
    }

    public abstract String getDescription();

    public boolean requiresAdmin() {
        return true;
    }

    public abstract boolean execute(CommandSender sender, String[] args);

    public String getUsage(String baseCommand) {
        return "/" + baseCommand + " " + getName();
    }
}
