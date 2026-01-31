package com.flame.api.scoreboard;

import com.flame.api.scoreboard.helper.ScoreboardHelper;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;

/**
 * author : s0ckett
 * date : 31.01.26
 */
public class FlameScoreboard implements ScoreboardHelper {

    private static final int MAX_LINE_LENGTH = 40;
    private static final int MAX_LINES = 15;

    private final Map<Integer, String> lines = new LinkedHashMap<>();
    private final Set<UUID> shownTo = new HashSet<>();
    private String title = "";
    private final Scoreboard scoreboard;
    private final Objective objective;
    private int uniqueColorIndex = 0;

    public FlameScoreboard() {

        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        this.objective = scoreboard.registerNewObjective("sidebar", "dummy");
        this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    @Override
    public void setTitle(String title) {
        if (title == null) {
            title = "";
        }
        this.title = ChatColor.translateAlternateColorCodes('&', title);

        if (this.title.length() > 32) {
            this.title = this.title.substring(0, 32);
        }

        objective.setDisplayName(this.title);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setLine(int line, String content) {
        if (content == null) {
            content = "";
        }

        if (line < 0 || line > MAX_LINES) {
            return;
        }

        String coloredContent = ChatColor.translateAlternateColorCodes('&', content);

        if (coloredContent.length() > MAX_LINE_LENGTH) {
            coloredContent = coloredContent.substring(0, MAX_LINE_LENGTH);
        }

        lines.put(line, coloredContent);
    }

    @Override
    public void setLines(List<String> lines) {
        if (lines == null || lines.isEmpty()) {
            return;
        }

        this.lines.clear();

        for (int i = 0; i < lines.size() && i < MAX_LINES; i++) {
            setLine(i, lines.get(i));
        }
    }

    @Override
    public String getLine(int line) {
        return lines.get(line);
    }

    @Override
    public Map<Integer, String> getLines() {
        return Collections.unmodifiableMap(lines);
    }

    @Override
    public void removeLine(int line) {
        String content = lines.remove(line);
        if (content != null) {
            scoreboard.resetScores(content);
        }
    }

    @Override
    public void setEmptyLine(int line) {
        StringBuilder emptyLine = new StringBuilder(ChatColor.RESET.toString());

        ChatColor[] colors = ChatColor.values();
        emptyLine.append(colors[uniqueColorIndex % colors.length]);
        uniqueColorIndex++;

        setLine(line, emptyLine.toString());
    }

    @Override
    public void update() {
        for (String entry : new HashSet<>(scoreboard.getEntries())) {
            scoreboard.resetScores(entry);
        }

        for (Map.Entry<Integer, String> entry : lines.entrySet()) {
            String content = entry.getValue();
            int score = entry.getKey();

            if (scoreboard.getEntries().contains(content)) {
                scoreboard.resetScores(content);
            }

            objective.getScore(content).setScore(score);
        }
    }

    @Override
    public void showTo(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        player.setScoreboard(scoreboard);
        shownTo.add(player.getUniqueId());
    }

    @Override
    public void hideFrom(Player player) {
        if (player == null) {
            return;
        }

        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        shownTo.remove(player.getUniqueId());
    }

    @Override
    public void broadcast() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            showTo(player);
        }
    }

    @Override
    public void clear() {
        for (String entry : new HashSet<>(scoreboard.getEntries())) {
            scoreboard.resetScores(entry);
        }

        lines.clear();
        uniqueColorIndex = 0;

        update();
    }

    @Override
    public boolean isShownTo(Player player) {
        return player != null && shownTo.contains(player.getUniqueId());
    }

    @Override
    public int getLineCount() {
        return lines.size();
    }

    /**
     * Установить строку с плейсхолдерами для игрока
     */
    public void setLineForPlayer(int line, String content, Player player) {
        if (content == null || player == null) {
            return;
        }

        String replaced = content
                .replace("{player}", player.getName())
                .replace("{world}", player.getWorld().getName())
                .replace("{health}", String.valueOf((int) player.getHealth()))
                .replace("{food}", String.valueOf(player.getFoodLevel()))
                .replace("{level}", String.valueOf(player.getLevel()));

        setLine(line, replaced);
    }

    /**
     * Обновить строку (удобно для анимок всяких)
     */
    public void updateLine(int line, String content) {
        setLine(line, content);
        update();
    }

    /**
     * Получить баккит скорборд
     */
    public Scoreboard getBukkitScoreboard() {
        return scoreboard;
    }

    public Objective getObjective() {
        return objective;
    }

    public Team createTeam(String name) {
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
        }
        return team;
    }

    public void removeTeam(String name) {
        Team team = scoreboard.getTeam(name);
        if (team != null) {
            team.unregister();
        }
    }

    public FlameScoreboard clone() {
        FlameScoreboard cloned = new FlameScoreboard();
        cloned.setTitle(this.title);

        for (Map.Entry<Integer, String> entry : this.lines.entrySet()) {
            cloned.setLine(entry.getKey(), entry.getValue());
        }

        cloned.update();
        return cloned;
    }

    public void clearTracking() {
        shownTo.clear();
    }

    public Set<UUID> getShownToPlayers() {
        return Collections.unmodifiableSet(shownTo);
    }

    @Override
    public String toString() {
        return "FlameScoreboard{" +
                "title='" + title + '\'' +
                ", lines=" + lines.size() +
                ", shownTo=" + shownTo.size() +
                '}';
    }
}