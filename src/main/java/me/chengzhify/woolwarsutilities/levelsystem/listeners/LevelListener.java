package me.chengzhify.woolwarsutilities.levelsystem.listeners;

import dev.pixelstudios.woolwars.api.events.arena.GameEndEvent;
import dev.pixelstudios.woolwars.api.events.arena.GameStartEvent;
import dev.pixelstudios.woolwars.api.events.arena.RoundEndEvent;
import dev.pixelstudios.woolwars.api.events.player.PlayerKillEvent;
import dev.pixelstudios.woolwars.api.events.player.PlayerAbilityEvent;
import dev.pixelstudios.woolwars.api.events.player.PlayerLeaveArenaEvent;
import me.chengzhify.woolwarsutilities.WoolWarsUtilities;
import me.chengzhify.woolwarsutilities.levelsystem.display.LevelFormatter;
import me.chengzhify.woolwarsutilities.levelsystem.storage.LevelManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class LevelListener implements Listener {
    private static HashMap<Player, Integer> expEarned = new HashMap<>();
    private static HashMap<Player, Integer> levelBefore = new HashMap<>();
    private static int onlineTimeTimerTaskId;

    @EventHandler
    public void onKill(PlayerKillEvent event) {
        Player killer = event.getAttacker().getPlayer();
        LevelManager.addExp(killer, 20);
        killer.sendMessage("§b+20 经验 (击杀)");
        expEarned.put(killer, expEarned.getOrDefault(killer, 0) + 20);
    }

    @EventHandler
    public void onGameStart(GameStartEvent event) {
        event.getArena().getPlayers().forEach(player -> expEarned.put(player.getPlayer(), 0));
        event.getArena().getPlayers().forEach(player -> levelBefore.put(player.getPlayer(), LevelManager.getData(player.getPlayer()).getLevel()));
        onlineTimeTimerTaskId = Bukkit.getScheduler().runTaskTimerAsynchronously(WoolWarsUtilities.getInstance(), () -> event.getArena().getPlayers().forEach(player -> {
            LevelManager.addExp(player.getPlayer(), 20);
            Bukkit.getScheduler().runTask(WoolWarsUtilities.getInstance(), () ->
                    player.send("§b+20 经验 (游玩时间)")
            );
        }), 20L * 60L, 20L * 60L).getTaskId();
    }

    @EventHandler
    public void onPlayerQuit(PlayerLeaveArenaEvent event) {
        if (expEarned.containsKey(event.getUser().getPlayer())) {
            expEarned.remove(event.getUser().getPlayer());
        }
    }


    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        Bukkit.getScheduler().cancelTask(onlineTimeTimerTaskId);
        event.getWinnerTeam().getMembers().forEach(user -> {
            Player player = user.getPlayer();
            LevelManager.addExp(player, 100);
            player.sendMessage("§b+100 经验 (游戏胜利)");
            expEarned.put(player, expEarned.getOrDefault(player, 0) + 100);
        });
        event.getLoserTeams().forEach(team -> {
           team.getMembers().forEach(user -> {
               Player player = user.getPlayer();
               LevelManager.addExp(player, 50);
               player.sendMessage("§b+50 经验 (游戏失败)");
               expEarned.put(player, expEarned.getOrDefault(player, 0) + 50);
           });
        });
        event.getArena().getPlayers().forEach(user -> {
            LevelManager.asyncSavePlayer(user.getPlayer());
        });
        Bukkit.getScheduler().runTaskLater(WoolWarsUtilities.getInstance(), () -> event.getArena().getPlayers().forEach(user -> {
            List<String> settlement =  new ArrayList<>();
            settlement.add(ChatColor.translateAlternateColorCodes('&', "&2-----------------------"));
            settlement.add(ChatColor.translateAlternateColorCodes('&', "&f&l        本场结算        "));
            settlement.add(ChatColor.translateAlternateColorCodes('&', "&f&l                       "));
            if (levelBefore.get(user.getPlayer()) != LevelManager.getData(user.getPlayer()).getLevel()) {
                settlement.add(ChatColor.translateAlternateColorCodes('&', "&b&l升级!" + LevelFormatter.getColoredLevel(LevelManager.getData(user.getPlayer()).getLevel(), LevelManager.getData(user.getPlayer()).getIcon())));
            }
            settlement.add(ChatColor.translateAlternateColorCodes('&', "&b等级 " + LevelManager.getData(user.getPlayer()).getLevel() + "            等级 " + (LevelManager.getData(user.getPlayer()).getLevel() + 1)));
            settlement.add(ChatColor.translateAlternateColorCodes('&', LevelManager.getProgressBar(LevelManager.getData(user.getPlayer()).getExp(), LevelManager.getData(user.getPlayer()).getExpToNextLevel(user.getPlayer().getLevel()), 10)));
            settlement.add(ChatColor.translateAlternateColorCodes('&', "&f你获得了 &a" + expEarned.get(user.getPlayer()) + " 羊毛战争经验"));
            settlement.add(ChatColor.translateAlternateColorCodes('&', "&2-----------------------"));
            user.send(settlement);
        }), 20L * 3L);
    }

    @EventHandler
    public void onUseAbility(PlayerAbilityEvent event) {
        Player player = event.getUser().getPlayer();
        LevelManager.addExp(player, 10);
        player.sendMessage("§b+10 经验 (使用技能)");
        expEarned.put(player, expEarned.getOrDefault(player, 0) + 10);
    }

    @EventHandler
    public void onRoundEnd(RoundEndEvent event) {
        if (event.getWinnerTeam() != null) {
            event.getWinnerTeam().getMembers().forEach(user -> {
                Player player = user.getPlayer();
                LevelManager.addExp(player, 50);
                player.sendMessage("§b+50 经验 (本回合胜利)");
                expEarned.put(player, expEarned.getOrDefault(player, 0) + 50);
            });
            event.getLoserTeams().forEach(team -> {
                team.getMembers().forEach(user -> {
                    Player player = user.getPlayer();
                    LevelManager.addExp(player, 25);
                    player.sendMessage("§b+25 经验 (本回合失败)");
                    expEarned.put(player, expEarned.getOrDefault(player, 0) + 25);
                });
            });
        }
    }
}