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