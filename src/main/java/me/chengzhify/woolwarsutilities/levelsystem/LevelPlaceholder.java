package me.chengzhify.woolwarsutilities.levelsystem;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;


public class LevelPlaceholder extends PlaceholderExpansion {


    @Override
    public String getIdentifier() {
        return "woolwars_utilities";
    }


    @Override
    public String getAuthor() {
        return "ChengZhiFy";
    }


    @Override
    public String getVersion() {
        return "1.0.0";
    }


    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) return "";
        var data = LevelManager.getData(player);
        if (data == null) return "";

        switch (identifier.toLowerCase()) {
            case "level":
                return String.valueOf(data.getLevel());
            case "rank":
            case "star":
                return LevelFormatter.getColoredLevel(data.getLevel());
            case "exp":
                return String.valueOf(data.getExp());
            case "next_exp":
                return String.valueOf(data.getExpToNextLevel(data.getLevel()));
            case "progress_bar": {
                int expToNext = data.getExpToNextLevel(data.getLevel());
                return LevelManager.getProgressBar(data.getExp(), expToNext, 10);
            }
            case "progress_percent": {
                int expToNext = data.getExpToNextLevel(data.getLevel());
                double percent = (double) data.getExp() / expToNext * 100.0;
                return String.format("%.1f%%", percent);
            }
            default:
                return "";
        }
    }
}