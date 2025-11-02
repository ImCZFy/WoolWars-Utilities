package me.chengzhify.woolwarsutilities.levelsystem;

import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.UUID;

public class MySQLManager {

    private static Connection connection;
    private static WoolWarsUtilities plugin;

    public static void connect(WoolWarsUtilities pluginInstance, String host, int port, String database, String user, String password) {
        plugin = pluginInstance;
        try {
            if (connection != null && !connection.isClosed()) return;

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                    + "?useSSL=false&autoReconnect=true&characterEncoding=UTF-8";
            connection = DriverManager.getConnection(url, user, password);

            try (Statement st = connection.createStatement()) {
                st.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS woolwars_levels (
                        uuid VARCHAR(36) PRIMARY KEY,
                        name VARCHAR(32),
                        level INT DEFAULT 1,
                        exp INT DEFAULT 0
                    )
                """);
            }

            plugin.getLogger().info("[MySQL] Connected and ensured table exists");
        } catch (SQLException e) {
            plugin.getLogger().severe("[MySQL] Connection failed: " + e.getMessage());
            e.printStackTrace();
        }

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, MySQLManager::keepAlive, 20 * 60 * 5, 20 * 60 * 5);
    }

    private static void keepAlive() {
        try {
            if (connection == null || connection.isClosed()) {
                plugin.getLogger().warning("[MySQL] Connection lost, reconnecting...");
                reconnect();
            } else {
                try (PreparedStatement ps = connection.prepareStatement("SELECT 1")) {
                    ps.executeQuery();
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[MySQL] Heartbeat failed: " + e.getMessage());
            reconnect();
        }
    }

    private static synchronized void reconnect() {
        try {
            plugin.getLogger().info("[MySQL] Attempting reconnection...");
            connect(plugin,
                    plugin.getConfig().getString("mysql.host"),
                    plugin.getConfig().getInt("mysql.port"),
                    plugin.getConfig().getString("mysql.database"),
                    plugin.getConfig().getString("mysql.user"),
                    plugin.getConfig().getString("mysql.password")
            );
        } catch (Exception e) {
            plugin.getLogger().severe("[MySQL] Reconnect failed: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLException("MySQL not connected");
        }
        return connection;
    }

    public static boolean playerExists(UUID uuid) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT 1 FROM woolwars_levels WHERE uuid = ? LIMIT 1")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[MySQL] playerExists(uuid) failed: " + e.getMessage());
            return false;
        }
    }

    public static boolean playerExists(String name) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT 1 FROM woolwars_levels WHERE name = ? LIMIT 1")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[MySQL] playerExists(name) failed: " + e.getMessage());
            return false;
        }
    }

    public static String getPlayerName(String uuid) {
        try (PreparedStatement stmt = getConnection().prepareStatement(
                "SELECT name FROM woolwars_levels WHERE uuid = ? LIMIT 1")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getString("name");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[MySQL] getPlayerName failed: " + e.getMessage());
        }
        return null;
    }

    public static String getPlayerUUID(String name) {
        String sql = "SELECT uuid FROM woolwars_levels WHERE LOWER(name)=LOWER(?) LIMIT 1";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("uuid");
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("[MySQL] getPlayerUUID failed: " + e.getMessage());
        }
        return null;
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("[MySQL] Connection closed.");
            }
        } catch (SQLException ignored) {}
    }
}
