package me.chengzhify.woolwarsutilities.listeners;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import dev.pixelstudios.woolwars.api.events.arena.GameEndEvent;
import dev.pixelstudios.woolwars.api.events.arena.GameStartEvent;
import dev.pixelstudios.woolwars.arena.data.Team;
import dev.pixelstudios.woolwars.storage.user.User;
import me.chengzhify.woolwarsutilities.impls.VoicechatImpl;
import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GameStateListener implements Listener {

    public static Map<String, UUID> teamGroups = new HashMap<>();

    @EventHandler
    public void onStart(GameStartEvent event) {
        if (VoicechatImpl.voiceServerApi == null) {
            WoolWarsUtilities.getInstance().getLogger().warning("[WoolWars - VoiceGroup] Voicechat API 未初始化！");
            return;
        }

        WoolWarsUtilities.getInstance().getLogger().info("[WoolWars - VoiceGroup] GameStartEvent 触发，正在创建语音频道...");

        List<Team> teams = event.getArena().getTeams();
        for (Team team : teams) {
            UUID groupId = VoicechatImpl.createGroup(event.getArena().getWorld().getName(), team.getName());
            Group group = VoicechatImpl.voiceServerApi.getGroup(groupId);
            teamGroups.put(event.getArena().getWorld().getName() + team.getName(), groupId);
            Bukkit.getScheduler().runTask(WoolWarsUtilities.getInstance(), () -> {
                WoolWarsUtilities.getInstance().getLogger().info("队伍 " + event.getArena().getWorld().getName() + team.getName() + " 成员数量: " + team.getMembers().size());
                for (User member : team.getMembers()) {
                    VoicechatConnection connection = VoicechatImpl.voiceServerApi.getConnectionOf(member.getUniqueId());
                    if (connection == null) {
                        WoolWarsUtilities.getInstance().getLogger().info("[WoolWars - VoiceGroup] 玩家 " + member.getName() + " 未连接到语音服务器！");
                        continue;
                    }

                    connection.setGroup(group);
                    WoolWarsUtilities.getInstance().getLogger().info("[WoolWars - VoiceGroup] 已将 " + member.getName() + " 分配至队伍 " + team.getName());
                    member.send("§a已将你分配至 §b" + team.getName() + " §a语音频道, 游戏结束后将自动退出!");
                }
            });
        }
        WoolWarsUtilities.getInstance().getLogger().info("[WoolWars - VoiceGroup] 竞技场 " + event.getArena().getWorld().getName() + " 的所有队伍语音组创建完毕。");
    }

    @EventHandler
    public void onEnd(GameEndEvent event) {
        if (VoicechatImpl.voiceServerApi == null) {
            WoolWarsUtilities.getInstance().getLogger().warning("[WoolWars - VoiceGroup] Voicechat API 未初始化！");
            return;
        }
        WoolWarsUtilities.getInstance().getLogger().info("[WoolWars - VoiceGroup] GameEndEvent 触发，正在移除语音组...");

        List<Team> teams = event.getArena().getTeams();
        for (Team team : teams) {
            UUID groupId = teamGroups.get(team.getName());
            if (groupId == null) continue;

            Group group = VoicechatImpl.voiceServerApi.getGroup(groupId);
            if (group == null) continue;

            for (User member : team.getMembers()) {
                VoicechatConnection connection = VoicechatImpl.voiceServerApi.getConnectionOf(member.getUniqueId());
                if (connection == null) continue;

                connection.setGroup(null);
                WoolWarsUtilities.getInstance().getLogger().info("[WoolWars - VoiceGroup] 已将 " + member.getName() + " 移出队伍语音频道");
                member.send("§c游戏结束，你已退出队伍语音频道。");
            }

            VoicechatImpl.voiceServerApi.removeGroup(group.getId());
            WoolWarsUtilities.getInstance().getLogger().info("[WoolWars - VoiceGroup] 队伍 " + event.getArena().getWorld().getName() + team.getName() + " 的语音组已删除。");
        }

        WoolWarsUtilities.getInstance().getLogger().info("[WoolWars - VoiceGroup] 竞技场 " + event.getArena().getWorld().getName() + " 的所有队伍语音组删除完毕。");

    }
}
