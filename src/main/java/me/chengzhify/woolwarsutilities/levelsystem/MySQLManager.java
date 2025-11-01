package me.chengzhify.woolwarsutilities.levelsystem;

import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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


    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) connection.close();
        } catch (SQLException ignored) {}
    }

}
