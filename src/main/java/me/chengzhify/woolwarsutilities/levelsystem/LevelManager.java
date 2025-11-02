package me.chengzhify.woolwarsutilities.levelsystem;

import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Consumer;


public class LevelManager {


    private static final Map<UUID, LevelData> cache = new HashMap<>();


    public static void asyncLoadPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(WoolWarsUtilities.getInstance(), () -> {
            try {
                Connection conn = MySQLManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("SELECT * FROM woolwars_levels WHERE uuid = ?");
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    int level = rs.getInt("level");
                    int exp = rs.getInt("exp");
                    cache.put(uuid, new LevelData(level, exp));
                } else {
                    cache.put(uuid, new LevelData(1, 0));
                    PreparedStatement insert = conn.prepareStatement("INSERT INTO woolwars_levels (uuid,name,level,exp) VALUES (?,?,1,0)");
                    insert.setString(1, uuid.toString());
                    insert.setString(2, player.getName());
                    insert.executeUpdate();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void asyncLoadPlayer(UUID uuid, String name, Consumer<LevelData> callback) {
        Bukkit.getScheduler().runTaskAsynchronously(WoolWarsUtilities.getInstance(), () -> {
            try {
                Connection conn = MySQLManager.getConnection();

                LevelData data;

                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM woolwars_levels WHERE uuid = ?")) {
                    ps.setString(1, uuid.toString());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            int level = rs.getInt("level");
                            int exp = rs.getInt("exp");
                            data = new LevelData(level, exp);
                        } else {
                            data = new LevelData(1, 0);
                            try (PreparedStatement insert = conn.prepareStatement(
                                    "INSERT INTO woolwars_levels (uuid, name, level, exp) VALUES (?, ?, 1, 0)")) {
                                insert.setString(1, uuid.toString());
                                insert.setString(2, name);
                                insert.executeUpdate();
                            }
                        }
                    }
                }

                cache.put(uuid, data);

                if (callback != null) {
                    LevelData finalData = data;
                    Bukkit.getScheduler().runTask(WoolWarsUtilities.getInstance(), () -> callback.accept(finalData));
                }

            } catch (SQLException e) {
                e.printStackTrace();
                if (callback != null) {
                    Bukkit.getScheduler().runTask(WoolWarsUtilities.getInstance(), () -> callback.accept(null));
                }
            }
        });
    }


    public static void asyncSavePlayer(Player player) {
        UUID uuid = player.getUniqueId();
        LevelData data = cache.get(uuid);
        if (data == null) return;
        Bukkit.getScheduler().runTaskAsynchronously(WoolWarsUtilities.getInstance(), () -> {
            try {
                var conn = MySQLManager.getConnection();
                PreparedStatement ps = conn.prepareStatement("UPDATE woolwars_levels SET level=?, exp=?, name=? WHERE uuid=?");
                ps.setInt(1, data.getLevel());
                ps.setInt(2, data.getExp());
                ps.setString(3, player.getName());
                ps.setString(4, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void asyncSavePlayer(UUID uuid, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(WoolWarsUtilities.getInstance(), () -> {
            LevelData data = cache.get(uuid);
            if (data == null) return;
            try {
                var conn = MySQLManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE woolwars_levels SET level = ?, exp = ?, name = ? WHERE uuid = ?"
                );
                ps.setInt(1, data.getLevel());
                ps.setInt(2, data.getExp());
                ps.setString(3, name);
                ps.setString(4, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        });
    }

    public static void asyncSavePlayer(UUID uuid, String name, Runnable callback) {
        Bukkit.getScheduler().runTaskAsynchronously(WoolWarsUtilities.getInstance(), () -> {
            LevelData data = cache.get(uuid);
            if (data == null) return;

            try {
                var conn = MySQLManager.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "UPDATE woolwars_levels SET level = ?, exp = ?, name = ? WHERE uuid = ?"
                );
                ps.setInt(1, data.getLevel());
                ps.setInt(2, data.getExp());
                ps.setString(3, name);
                ps.setString(4, uuid.toString());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (callback != null) {
                Bukkit.getScheduler().runTask(WoolWarsUtilities.getInstance(), callback);
            }
        });
    }


    public static LevelData getData(UUID uuid) {
        return cache.get(uuid);
    }


    public static LevelData getData(Player player) {
        return getData(player.getUniqueId());
    }


    public static void addExp(Player player, int amount) {
        UUID uuid = player.getUniqueId();
        LevelData data = cache.get(uuid);
        if (data == null) {
            data = new LevelData(1, 0);
            cache.put(uuid, data);
        }
        int before = data.getLevel();
        data.addExp(amount);
        if (data.getLevel() > before) {
            LevelData finalData = data;
            Bukkit.getScheduler().runTask(WoolWarsUtilities.getInstance(), () -> {
                player.sendMessage("§aYou leveled up! New: " + LevelFormatter.getColoredLevel(finalData.getLevel()));
            });
        }
    }


    public static void saveAllSync() {
        try {
            var conn = MySQLManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("INSERT INTO woolwars_levels (uuid,name,level,exp) VALUES (?,?,?,?) ON DUPLICATE KEY UPDATE name=VALUES(name), level=VALUES(level), exp=VALUES(exp)");
            for (Map.Entry<UUID, LevelData> e : cache.entrySet()) {
                ps.setString(1, e.getKey().toString());
                Player p = WoolWarsUtilities.getInstance().getServer().getPlayer(e.getKey());
                ps.setString(2, p != null ? p.getName() : "Unknown");
                ps.setInt(3, e.getValue().getLevel());
                ps.setInt(4, e.getValue().getExp());
                ps.addBatch();
            }
            ps.executeBatch();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


/*    public static List<Map.Entry<UUID, LevelData>> getTop(int limit) {
        try {
            var conn = MySQLManager.getConnection();
            PreparedStatement ps = conn.prepareStatement("SELECT uuid, level, exp FROM woolwars_levels ORDER BY level DESC, exp DESC LIMIT ?");
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            List<Map.Entry<UUID, LevelData>> list = new ArrayList<>();
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                LevelData data = new LevelData(rs.getInt("level"), rs.getInt("exp"));
                list.add(new AbstractMap.SimpleEntry<>(uuid, data));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
*/
    public static String getProgressBar(int currentExp, int expToNextLevel, int totalBars) {
        double percent = (double) currentExp / expToNextLevel;
        int progressBars = (int) Math.round(totalBars * percent);

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < totalBars; i++) {
            if (i < progressBars) {
                bar.append("&b■");
            } else {
                bar.append("&7■");
            }
        }
        return bar.toString();
    }
}