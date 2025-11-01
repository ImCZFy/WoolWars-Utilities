package me.chengzhify.woolwarsutilities.listeners;

import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import dev.pixelstudios.woolwars.api.events.player.PlayerLeaveArenaEvent;
import dev.pixelstudios.woolwars.arena.data.Team;
import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import me.chengzhify.woolwarsutilities.impls.VoicechatImpl;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.UUID;

public class LeaveGameListener implements Listener {

    @EventHandler
    public void onLeave(PlayerLeaveArenaEvent event) {
        VoicechatConnection connection = VoicechatImpl.voiceServerApi.getConnectionOf(event.getUser().getUniqueId());
        if (connection == null) {
            return;
        }
        if (connection.isInGroup()) {
            connection.setGroup(null);
            event.getUser().send("§c因退出游戏, 你已自动退出队伍语音频道!");
        }
        List<Team> teams = event.getArena().getTeams();
        Bukkit.getScheduler().runTask(WoolWarsUtilities.getInstance(), () -> {
            for (Team team : teams) {
                if (team.getMembers().isEmpty()) {
                    WoolWarsUtilities.getInstance().getLogger().info("[WoolWars - VoiceGroup] 队伍 " + event.getArena().getWorld().getName() + team.getName() + " 的语音组所有人已退出, 已删除该语音组。");
                    UUID groupId = GameStateListener.teamGroups.get(event.getArena().getWorld().getName() + team.getName());
                    VoicechatImpl.voiceServerApi.removeGroup(groupId);
                }
            }
            });
        }
    }