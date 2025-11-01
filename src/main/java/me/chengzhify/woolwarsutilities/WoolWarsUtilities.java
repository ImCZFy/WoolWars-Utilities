package me.chengzhify.woolwarsutilities;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import me.chengzhify.woolwarsutilities.impls.VoicechatImpl;
import me.chengzhify.woolwarsutilities.listeners.GameStateListener;
import me.chengzhify.woolwarsutilities.listeners.LeaveGameListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
        if (getConfig().getBoolean("voice-chat")) {
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
    }

    @Override
    public void onDisable() {
        if (voicechatPlugin != null) {
            getServer().getServicesManager().unregister(voicechatPlugin);
        }
    }
    public static WoolWarsUtilities getInstance() {
        return instance;
    }

}
