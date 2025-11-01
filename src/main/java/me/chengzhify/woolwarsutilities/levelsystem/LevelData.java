package me.chengzhify.woolwarsutilities.levelsystem;

public class LevelData {

    private int level;
    private int exp;


    public LevelData(int level, int exp) {
        this.level = level;
        this.exp = exp;
    }


    public int getLevel() {
        return level;
    }


    public int getExp() {
        return exp;
    }


    public void addExp(int amount) {
        exp += amount;
        while (exp >= getExpToNextLevel(level)) {
            exp -= getExpToNextLevel(level);
            level++;
        }
    }


    public int getExpToNextLevel(int level) {
        if (level <= 10) return 500;
        if (level % 100 == 0) return 500;
        return 1000;
    }
}