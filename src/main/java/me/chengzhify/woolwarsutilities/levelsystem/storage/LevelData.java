package me.chengzhify.woolwarsutilities.levelsystem.storage;

public class LevelData {

    private int level;
    private int exp;
    private String icon;


    public LevelData(int level, int exp, String icon) {
        this.level = level;
        this.exp = exp;
        this.icon = icon;
    }


    public int getLevel() {
        return level;
    }


    public int getExp() {
        return exp;
    }

    public String getIcon() {
        return icon;
    }

    public void addExp(int amount) {
        exp += amount;
        while (exp >= getExpToNextLevel(level)) {
            exp -= getExpToNextLevel(level);
            level++;
        }
    }

    public void addLevel(int amount) {
        level += amount;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExp(int exp) {
        if (exp <= getExpToNextLevel(level)) {
            this.exp = exp;
        } else {
            setExp(0);
            addExp(exp);
        }
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void reset() {
        this.level = 0;
        this.exp = 0;
    }

    public int getExpToNextLevel(int level) {
        if (level <= 10) return 500;
        if (level % 100 == 0) return 500;
        return 1000;
    }
}