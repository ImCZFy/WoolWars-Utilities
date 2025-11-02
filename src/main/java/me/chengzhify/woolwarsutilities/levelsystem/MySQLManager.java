package me.chengzhify.woolwarsutilities.levelsystem;

import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.UUID;

public class MySQLManager {
    private static Connection connection;
    private static WoolWarsUtilities plugin;

    public static void connect(WoolWarsUtilities pluginInstance, String host, int port, String database, String user, String password) {
        plugin = pluginInstance;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                if (connection != null && !connection.isClosed()) return;
                String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true&characterEncoding=UTF-8";
                connection = DriverManager.getConnection(url, user, password);
                Statement st = connection.createStatement();
                st.executeUpdate("CREATE TABLE IF NOT EXISTS woolwars_levels (uuid VARCHAR(36) PRIMARY KEY, name VARCHAR(32), level INT DEFAULT 1, exp INT DEFAULT 0)");
                plugin.getLogger().info("[MySQL] Connected and ensured table exists");
            } catch (SQLException e) {
                plugin.getLogger().severe("[MySQL] Connection failed: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }


    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) throw new SQLException("MySQL not connected");
        return connection;
    }

    public static boolean playerExists(UUID uuid) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM woolwars_levels WHERE uuid = ? LIMIT 1")) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean playerExists(String name) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM woolwars_levels WHERE name = ? LIMIT 1")) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String getPlayerName(String name) {
        String query = "SELECT name FROM player_levels WHERE name = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getPlayerName(UUID uuid) {
        String query = "SELECT name FROM player_levels WHERE uuid = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, uuid != null ? uuid.toString() : "");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UUID getPlayerUUID(UUID uuid) {
        String query = "SELECT uuid FROM player_levels WHERE uuid = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, uuid != null ? uuid.toString() : "");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("uuid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static UUID getPlayerUUID(String name) {
        String query = "SELECT uuid FROM player_levels WHERE name = ? LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("uuid"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }

}