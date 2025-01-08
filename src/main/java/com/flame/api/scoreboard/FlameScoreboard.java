package com.flame.api.scoreboard;

import com.flame.api.scoreboard.helper.ScoreboardHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class FlameScoreboard implements ScoreboardHelper {

    /**
     * author : s0ckett
     * date : 09.01.25
     */

    private final Map<Integer, String> lines = new HashMap<>();
    private String title = "";
    private final Scoreboard scoreboard;
    private final Objective objective;
    private int uniqueId = 0;

    public FlameScoreboard() {
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("sidebar", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public void setTitle(String title) {
        this.title = ChatColor.translateAlternateColorCodes('&', title);
        objective.setDisplayName(this.title);
    }

    @Override
    public void setLine(int line, String content) {
        lines.put(line, ChatColor.translateAlternateColorCodes('&', content));
    }

    /**
     * Добавляет уникальную "пустую" строку на заданной позиции.
     */
    public void setEmptyLine(int line) {
        String emptyLine = ChatColor.RESET + "" + ChatColor.values()[uniqueId % ChatColor.values().length] + " ";
        uniqueId++;
        setLine(line, emptyLine);
    }

    @Override
    public void removeLine(int line) {
        lines.remove(line);
    }

    @Override
    public void update() {
        scoreboard.getEntries().forEach(scoreboard::resetScores);

        for (Map.Entry<Integer, String> entry : lines.entrySet()) {
            objective.getScore(entry.getValue()).setScore(entry.getKey());
        }
    }

    @Override
    public void showTo(Player player) {
        player.setScoreboard(scoreboard);
    }

    @Override
    public void broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::showTo);
    }

    @Override
    public void clear() {
        scoreboard.clearSlot(DisplaySlot.SIDEBAR);
        lines.clear();
    }
}