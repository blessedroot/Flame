package com.flame.api.scoreboard.manager;

import com.flame.api.scoreboard.FlameScoreboard;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {

    /**
     * author : s0ckett
     * date : 09.01.25
     */

    private final Map<Player, FlameScoreboard> playerScoreboards = new HashMap<>();
    private final FlameScoreboard globalScoreboard = new FlameScoreboard();

    /**
     * Получить глобальный скорборд.
     */
    public FlameScoreboard getGlobalScoreboard() {
        return globalScoreboard;
    }

    /**
     * Получить персональный скорборд для игрока.
     */
    public FlameScoreboard getPlayerScoreboard(Player player) {
        return playerScoreboards.computeIfAbsent(player, p -> new FlameScoreboard());
    }

    /**
     * Очистить персональный скорборд игрока.
     */
    public void clearPlayerScoreboard(Player player) {
        if (playerScoreboards.containsKey(player)) {
            playerScoreboards.get(player).clear();
            playerScoreboards.remove(player);
        }
    }

    /**
     * Очистить глобальный скорборд.
     */
    public void clearGlobalScoreboard() {
        globalScoreboard.clear();
    }
}
