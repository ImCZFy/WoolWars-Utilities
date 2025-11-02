package me.chengzhify.woolwarsutilities.voicegroup.listeners;

import de.maxhenkel.voicechat.api.VoicechatConnection;
import dev.pixelstudios.woolwars.api.events.player.PlayerLeaveArenaEvent;
import dev.pixelstudios.woolwars.arena.data.Team;
import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import me.chengzhify.woolwarsutilities.voicegroup.VoicechatImpl;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

import static me.chengzhify.woolwarsutilities.WoolWarsUtilities.getInstance;

public class LeaveGameListener implements Listener {
    private static final WoolWarsUtilities instance = WoolWarsUtilities.getInstance();
    private static final boolean voiceLog = instance.getConfig().getBoolean("voice-group-console-log");
    @EventHandler
    public void onLeave(PlayerLeaveArenaEvent event) {
        VoicechatConnection connection = VoicechatImpl.voiceServerApi.getConnectionOf(event.getUser().getUniqueId());
        if (connection == null) {
            return;
        }
        if (connection.isInGroup()) {
            connection.setGroup(null);
            event.getUser().send("§c因退出游戏, 你已自动退出队伍语音组!");
        }
        List<Team> teams = event.getArena().getTeams();
        Bukkit.getScheduler().runTask(getInstance(), () -> {
            for (Team team : teams) {
                if (team.getMembers().isEmpty()) {
                    if (voiceLog) getInstance().getLogger().info("[WoolWars - VoiceGroup] 队伍 " + event.getArena().getWorld().getName() + team.getProperties().getName() + " 的语音组所有人已退出, 已删除该语音组。");
                    UUID groupId = GameStateListener.teamGroups.get(event.getArena().getWorld().getName() + team.getProperties().getName());
                    VoicechatImpl.voiceServerApi.removeGroup(groupId);
                }
            }
            });
        }
    }