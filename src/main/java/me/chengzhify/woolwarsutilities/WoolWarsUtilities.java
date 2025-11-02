package me.chengzhify.woolwarsutilities;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import me.chengzhify.woolwarsutilities.levelsystem.LevelFormatter;
import me.chengzhify.woolwarsutilities.levelsystem.LevelManager;
import me.chengzhify.woolwarsutilities.levelsystem.LevelPlaceholder;
import me.chengzhify.woolwarsutilities.levelsystem.MySQLManager;
import me.chengzhify.woolwarsutilities.levelsystem.commands.LevelCommand;
import me.chengzhify.woolwarsutilities.levelsystem.listeners.LevelListener;
import me.chengzhify.woolwarsutilities.voicegroup.VoicechatImpl;
import me.chengzhify.woolwarsutilities.voicegroup.listeners.GameStateListener;
import me.chengzhify.woolwarsutilities.voicegroup.listeners.LeaveGameListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nullable;

public final class WoolWarsUtilities extends JavaPlugin {
    public static final String PLUGIN_ID = "woolwars_voicegroup";
    public static final Logger LOGGER = LogManager.getLogger(PLUGIN_ID);
    private static WoolWarsUtilities instance;
    @Nullable
    private VoicechatImpl voicechatPlugin;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        if (getConfig().getBoolean("voice-group")) {
            if (!getServer().getPluginManager().isPluginEnabled("voicechat")) {
                getLogger().severe("voicechat not found! Disabling...");
                getServer().getPluginManager().disablePlugin(this);
            } else {
                BukkitVoicechatService service = getServer().getServicesManager().load(BukkitVoicechatService.class);
                if (service != null) {
                    voicechatPlugin = new VoicechatImpl();
                    service.registerPlugin(voicechatPlugin);
                    LOGGER.info("Successfully registered woolwars voicegroup plugin");
                } else {
                    LOGGER.info("Failed to register woolwars voicegroup plugin");
                }
            }
        } else {
            LOGGER.info("Voicechat addon is not enabled in your config. If you need that function, please edit your config.");
        }

        if (!getServer().getPluginManager().isPluginEnabled("WoolWars")) {
            getLogger().severe("WoolWars not found! Disabling...");
            getServer().getPluginManager().disablePlugin(this);
        }

        getServer().getPluginManager().registerEvents(new GameStateListener(), this);
        getServer().getPluginManager().registerEvents(new LeaveGameListener(), this);
        if (getConfig().getBoolean("mysql.enable")) {
            String host = getConfig().getString("mysql.host", "localhost");
            int port = getConfig().getInt("mysql.port", 3306);
            String database = getConfig().getString("mysql.database", "woolwars_utilities");
            String user = getConfig().getString("mysql.user", "root");
            String password = getConfig().getString("mysql.password", "");
            MySQLManager.connect(this, host, port, database, user, password);
            LevelFormatter.loadColors(this);
            getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onJoin(org.bukkit.event.player.PlayerJoinEvent e) {
                    LevelManager.asyncLoadPlayer(e.getPlayer());
                }


                @EventHandler
                public void onQuit(org.bukkit.event.player.PlayerQuitEvent e) {
                    LevelManager.asyncSavePlayer(e.getPlayer());
                }
            }, this);


            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                new LevelPlaceholder().register();
                getLogger().info("Registered LevelPlaceholder with PlaceholderAPI");
            }
            getServer().getPluginManager().registerEvents(new LevelListener(), this);
            getCommand("wwlevel").setExecutor(new LevelCommand());
        }
    }




    @Override
    public void onDisable() {
        if (voicechatPlugin != null) {
            getServer().getServicesManager().unregister(voicechatPlugin);
        }
        LevelManager.saveAllSync();
        MySQLManager.disconnect();
        instance = null;
    }
    public static WoolWarsUtilities getInstance() {
        return instance;
    }

}
