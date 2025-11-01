package me.chengzhify.woolwarsutilities.levelsystem;

import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;


import java.util.LinkedHashMap;
import java.util.Map;


public class LevelFormatter {


    private static final Map<String, String> colorRanges = new LinkedHashMap<>();
    private static final String icon = WoolWarsUtilities.getInstance().getConfig().getString("level-system.icon");

    public static void loadColors(JavaPlugin plugin) {
        colorRanges.clear();
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("level-colors");
        if (section == null) return;
        for (String key : section.getKeys(false)) {
            colorRanges.put(key, section.getString(key));
        }
        plugin.getLogger().info("[WoolWars-Utilities, Level System] Loaded " + colorRanges.size() + " color ranges.");
    }


    public static String getColoredLevel(int level) {
        for (Map.Entry<String, String> entry : colorRanges.entrySet()) {
            String range = entry.getKey();
            String colorCode = entry.getValue();


            if (range.contains("-")) {
                String[] split = range.split("-");
                int min = Integer.parseInt(split[0]);
                int max = Integer.parseInt(split[1]);
                if (level >= min && level <= max) {
                    return ChatColor.translateAlternateColorCodes('&', colorCode + "[" + level + icon + "]");
                }
            } else if (range.endsWith("+")) {
                int min = Integer.parseInt(range.replace("+", ""));
                if (level >= min) {
                    if ("rainbow".equalsIgnoreCase(colorCode)) {
                        return getRainbow("[" + level + icon + "]");
                    } else {
                        return ChatColor.translateAlternateColorCodes('&', colorCode + "[" + level + icon + "]");
                    }
                }
            }
        }
        return ChatColor.GRAY + "[" + level + icon + "]";
    }


    private static String getRainbow(String text) {
        ChatColor[] colors = {
                ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW,
                ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE, ChatColor.LIGHT_PURPLE
        };
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (char c : text.toCharArray()) {
            sb.append(colors[i % colors.length]).append(c);
            i++;
        }
        return sb.toString();
    }
}