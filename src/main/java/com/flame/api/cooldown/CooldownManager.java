package com.flame.api.cooldown;

import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * author : s0ckett
 * date : 30.01.26
 * комменты потом, впадлу делать
 */
public class CooldownManager {

    private static final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    private static long lastCleanup = System.currentTimeMillis();
    private static final long CLEANUP_INTERVAL = TimeUnit.MINUTES.toMillis(5);

    public static boolean check(Player player, String key, long seconds) {
        if (player == null || key == null) {
            return false;
        }
        return timeLeft(player, key) <= 0 || reset(player, key, seconds);
    }

    public static boolean checkMillis(Player player, String key, long millis) {
        if (player == null || key == null) {
            return false;
        }
        return timeLeftMillis(player, key) <= 0 || resetMillis(player, key, millis);
    }

    public static long timeLeft(Player player, String key) {
        return timeLeftMillis(player, key) / 1000;
    }

    public static long timeLeftMillis(Player player, String key) {
        if (player == null || key == null) {
            return 0L;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) {
            return 0L;
        }

        Long endTime = playerCooldowns.get(key);
        if (endTime == null) {
            return 0L;
        }

        long remaining = endTime - System.currentTimeMillis();
        return Math.max(0L, remaining);
    }

    public static boolean reset(Player player, String key, long seconds) {
        return resetMillis(player, key, seconds * 1000);
    }

    public static boolean resetMillis(Player player, String key, long millis) {
        if (player == null || key == null || millis < 0) {
            return false;
        }

        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>())
                .put(key, System.currentTimeMillis() + millis);

        periodicCleanup();
        return true;
    }

    public static boolean has(Player player, String key) {
        return timeLeftMillis(player, key) > 0;
    }

    public static void clear(Player player, String key) {
        if (player == null || key == null) {
            return;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns != null) {
            playerCooldowns.remove(key);

            // Удаляем пустую карту
            if (playerCooldowns.isEmpty()) {
                cooldowns.remove(player.getUniqueId());
            }
        }
    }

    public static void clearAll(Player player) {
        if (player != null) {
            cooldowns.remove(player.getUniqueId());
        }
    }

    public static void extend(Player player, String key, long seconds) {
        extendMillis(player, key, seconds * 1000);
    }

    public static void extendMillis(Player player, String key, long millis) {
        if (player == null || key == null || millis < 0) {
            return;
        }

        cooldowns.computeIfAbsent(player.getUniqueId(), k -> new ConcurrentHashMap<>())
                .compute(key, (k, currentEnd) -> {
                    long now = System.currentTimeMillis();
                    if (currentEnd == null || currentEnd < now) {
                        // Кулдаун истёк, создаём новый
                        return now + millis;
                    } else {
                        // Продлеваем существующий
                        return currentEnd + millis;
                    }
                });
    }

    public static void reduce(Player player, String key, long seconds) {
        reduceMillis(player, key, seconds * 1000);
    }

    public static void reduceMillis(Player player, String key, long millis) {
        if (player == null || key == null || millis < 0) {
            return;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) {
            return;
        }

        playerCooldowns.computeIfPresent(key, (k, endTime) -> {
            long newEndTime = endTime - millis;
            // Если кулдаун истёк, удаляем его
            return newEndTime > System.currentTimeMillis() ? newEndTime : null;
        });
    }

    public static String getFormatted(Player player, String key) {
        long millis = timeLeftMillis(player, key);
        if (millis <= 0) {
            return "0s";
        }
        return String.format(Locale.US, "%.1fs", millis / 1000.0);
    }

    public static String getFormattedDetailed(Player player, String key) {
        long millis = timeLeftMillis(player, key);
        if (millis <= 0) {
            return "0s";
        }

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        StringBuilder result = new StringBuilder();

        if (days > 0) {
            result.append(days).append("d ");
            hours %= 24;
        }
        if (hours > 0) {
            result.append(hours).append("h ");
            minutes %= 60;
        }
        if (minutes > 0) {
            result.append(minutes).append("m ");
            seconds %= 60;
        }
        if (seconds > 0 || result.length() == 0) {
            result.append(seconds).append("s");
        }

        return result.toString().trim();
    }

    public static String getFormattedCompact(Player player, String key) {
        long millis = timeLeftMillis(player, key);
        if (millis <= 0) {
            return "0:00";
        }

        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    public static double getProgress(Player player, String key, long totalDuration) {
        if (totalDuration <= 0) {
            return 0.0;
        }

        long remaining = timeLeftMillis(player, key);
        if (remaining <= 0) {
            return 0.0;
        }

        return Math.min(1.0, (double) remaining / totalDuration);
    }

    public static Map<String, Long> getAll(Player player) {
        if (player == null) {
            return Collections.emptyMap();
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null || playerCooldowns.isEmpty()) {
            return Collections.emptyMap();
        }

        // Фильтруем истёкшие кулдауны
        Map<String, Long> active = new HashMap<>();
        long now = System.currentTimeMillis();

        for (Map.Entry<String, Long> entry : playerCooldowns.entrySet()) {
            long remaining = entry.getValue() - now;
            if (remaining > 0) {
                active.put(entry.getKey(), remaining);
            }
        }

        return Collections.unmodifiableMap(active);
    }

    public static boolean hasAny(Player player) {
        if (player == null) {
            return false;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null || playerCooldowns.isEmpty()) {
            return false;
        }

        long now = System.currentTimeMillis();
        return playerCooldowns.values().stream().anyMatch(endTime -> endTime > now);
    }

    public static int getActiveCount(Player player) {
        return getAll(player).size();
    }

    public static boolean copy(Player player, String sourceKey, String targetKey) {
        if (player == null || sourceKey == null || targetKey == null) {
            return false;
        }

        long remaining = timeLeftMillis(player, sourceKey);
        if (remaining <= 0) {
            return false;
        }

        return resetMillis(player, targetKey, remaining);
    }

    public static void cleanupExpired() {
        long now = System.currentTimeMillis();

        cooldowns.entrySet().removeIf(entry -> {
            Map<String, Long> playerCooldowns = entry.getValue();

            playerCooldowns.entrySet().removeIf(cooldown -> cooldown.getValue() <= now);

            return playerCooldowns.isEmpty();
        });
    }

    public static void clearAllPlayers() {
        cooldowns.clear();
    }

    public static int getPlayerCount() {
        return cooldowns.size();
    }

    public static int getTotalCooldownCount() {
        return cooldowns.values().stream()
                .mapToInt(Map::size)
                .sum();
    }

    private static void periodicCleanup() {
        long now = System.currentTimeMillis();
        if (now - lastCleanup > CLEANUP_INTERVAL) {
            cleanupExpired();
            lastCleanup = now;
        }
    }

    public static long getEndTime(Player player, String key) {
        if (player == null || key == null) {
            return 0L;
        }

        Map<String, Long> playerCooldowns = cooldowns.get(player.getUniqueId());
        if (playerCooldowns == null) {
            return 0L;
        }

        return playerCooldowns.getOrDefault(key, 0L);
    }

    public static boolean isExpired(Player player, String key) {
        return timeLeftMillis(player, key) <= 0;
    }
}