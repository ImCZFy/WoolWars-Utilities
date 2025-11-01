package me.chengzhify.woolwarsutilities.levelsystem.listeners;

import dev.pixelstudios.woolwars.api.events.arena.GameEndEvent;
import dev.pixelstudios.woolwars.api.events.player.PlayerKillEvent;
import dev.pixelstudios.woolwars.api.events.player.PlayerAbilityEvent;
import me.chengzhify.woolwarsutilities.levelsystem.LevelManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class LevelListener implements Listener {


    @EventHandler
    public void onKill(PlayerKillEvent event) {
        Player killer = event.getAttacker().getPlayer();
        LevelManager.addExp(killer, 20);
        killer.sendMessage("§b+20 经验 (击杀)");
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {
        event.getWinnerTeam().getMembers().forEach(user -> {
            Player player = user.getPlayer();
            LevelManager.addExp(player, 100);
            player.sendMessage("§b+100 经验 (游戏胜利)");
        });
    }

    @EventHandler
    public void onUseAbility(PlayerAbilityEvent event) {
        Player player = event.getUser().getPlayer();
        LevelManager.addExp(player, 10);
        player.sendMessage("§b+20 经验 (使用技能)");
    }
}