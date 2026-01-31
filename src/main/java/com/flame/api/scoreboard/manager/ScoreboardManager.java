package com.flame.api.scoreboard.manager;

import com.flame.api.FlameAPIPlugin;
import com.flame.api.scoreboard.FlameScoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * author : s0ckett
 * date : 31.01.26
 */
public class ScoreboardManager implements Listener {

    private final Map<UUID, FlameScoreboard> playerScoreboards = new ConcurrentHashMap<>();
    private FlameScoreboard globalScoreboard;
    public FlameAPIPlugin flameAPIPlugin;
    private BukkitTask autoUpdateTask;
    private Consumer<Player> scoreboardUpdater;
    private long updateInterval = 20L; // 1 секунда по умолчанию

    public ScoreboardManager() {
        if (flameAPIPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.globalScoreboard = new FlameScoreboard();
        Bukkit.getPluginManager().registerEvents(this, flameAPIPlugin);
    }

    /**
     * Получить глобальный скорборд
     * @return глобальный скорборд
     */
    public FlameScoreboard getGlobalScoreboard() {
        return globalScoreboard;
    }

    /**
     * Установить новый глобальный скорборд
     * @param scoreboard новый скорборд
     */
    public void setGlobalScoreboard(FlameScoreboard scoreboard) {
        if (scoreboard == null) {
            throw new IllegalArgumentException("Scoreboard cannot be null");
        }
        this.globalScoreboard = scoreboard;
    }

    /**
     * Получить персональный скорборд игрока
     * @param player игрок
     * @return скорборд игрока
     */
    public FlameScoreboard getPlayerScoreboard(Player player) {
        if (player == null) {
            return null;
        }
        return playerScoreboards.computeIfAbsent(player.getUniqueId(), uuid -> new FlameScoreboard());
    }

    /**
     * Установить персональный скорборд игроку
     * @param player игрок
     * @param scoreboard скорборд
     */
    public void setPlayerScoreboard(Player player, FlameScoreboard scoreboard) {
        if (player == null || scoreboard == null) {
            return;
        }
        playerScoreboards.put(player.getUniqueId(), scoreboard);
        scoreboard.showTo(player);
    }

    /**
     * Проверить есть ли у игрока персональный скорборд
     * @param player игрок
     * @return true если есть
     */
    public boolean hasPlayerScoreboard(Player player) {
        return player != null && playerScoreboards.containsKey(player.getUniqueId());
    }

    /**
     * Очистить персональный скорборд игрока
     * @param player игрок
     */
    public void clearPlayerScoreboard(Player player) {
        if (player == null) {
            return;
        }

        FlameScoreboard scoreboard = playerScoreboards.remove(player.getUniqueId());
        if (scoreboard != null) {
            scoreboard.clear();
            scoreboard.hideFrom(player);
        }
    }

    /**
     * Очистить глобальный скорборд
     */
    public void clearGlobalScoreboard() {
        globalScoreboard.clear();
    }

    /**
     * Очистить все скорборды
     */
    public void clearAll() {
        for (FlameScoreboard scoreboard : playerScoreboards.values()) {
            scoreboard.clear();
        }
        playerScoreboards.clear();

        globalScoreboard.clear();
    }

    /**
     * Показать глобальный скорборд всем
     */
    public void showGlobalToAll() {
        globalScoreboard.broadcast();
    }

    /**
     * Показать глобальный скорборд игроку
     * @param player игрок
     */
    public void showGlobalTo(Player player) {
        if (player != null) {
            globalScoreboard.showTo(player);
        }
    }

    /**
     * Скрыть скорборд у игрока
     * @param player игрок
     */
    public void hideFrom(Player player) {
        if (player == null) {
            return;
        }

        FlameScoreboard personal = playerScoreboards.get(player.getUniqueId());
        if (personal != null) {
            personal.hideFrom(player);
        } else {
            globalScoreboard.hideFrom(player);
        }
    }

    /**
     * Получить активный скорборд для игрока (персональный или глобальный)
     * @param player игрок
     * @return активный скорборд
     */
    public FlameScoreboard getActiveScoreboard(Player player) {
        if (player == null) {
            return null;
        }

        FlameScoreboard personal = playerScoreboards.get(player.getUniqueId());
        return personal != null ? personal : globalScoreboard;
    }

    /**
     * Получить кол-во игроков с персональными скорбордами
     * @return кол-во игроков
     */
    public int getPlayerScoreboardCount() {
        return playerScoreboards.size();
    }

    /**
     * Получить все персональные скорборды
     * @return неизменяемая мапа скорбордов
     */
    public Map<UUID, FlameScoreboard> getAllPlayerScoreboards() {
        return Collections.unmodifiableMap(playerScoreboards);
    }

    /**
     * Включить автоматическое обновление скорбордов
     * @param updater функция обновления для каждого игрока
     * @param interval интервал обновления в тиках
     */
    public void enableAutoUpdate(Consumer<Player> updater, long interval) {
        if (updater == null) {
            throw new IllegalArgumentException("Updater cannot be null");
        }

        this.scoreboardUpdater = updater;
        this.updateInterval = interval;

        if (autoUpdateTask != null) {
            autoUpdateTask.cancel();
        }

        autoUpdateTask = Bukkit.getScheduler().runTaskTimer(flameAPIPlugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                try {
                    updater.accept(player);
                } catch (Exception e) {
                    flameAPIPlugin.getLogger().warning("Error updating scoreboard for " + player.getName() + ": " + e.getMessage());
                }
            }
        }, 0L, interval);
    }

    /**
     * Отключить автоматическое обновление
     */
    public void disableAutoUpdate() {
        if (autoUpdateTask != null) {
            autoUpdateTask.cancel();
            autoUpdateTask = null;
        }
        scoreboardUpdater = null;
    }

    /**
     * Проверить включено ли автообновление
     * @return true если включено
     */
    public boolean isAutoUpdateEnabled() {
        return autoUpdateTask != null;
    }

    /**
     * Получить интервал автообновления
     * @return интервал в тиках
     */
    public long getUpdateInterval() {
        return updateInterval;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        clearPlayerScoreboard(player);
    }

    public void shutdown() {
        disableAutoUpdate();

        clearAll();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        }
    }
}