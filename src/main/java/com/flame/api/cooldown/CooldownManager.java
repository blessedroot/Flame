package com.flame.api.cooldown;

import org.bukkit.entity.Player;

import java.util.*;

public class CooldownManager {

    private static final Map<UUID, Map<String, Long>> data = new HashMap<>();

    public static boolean check(Player p, String key, long seconds) {
        return timeLeft(p, key) <= 0 || reset(p, key, seconds);
    }

    public static boolean checkMillis(Player p, String key, long millis) {
        return timeLeftMillis(p, key) <= 0 || resetMillis(p, key, millis);
    }

    public static long timeLeft(Player p, String key) {
        return timeLeftMillis(p, key) / 1000;
    }

    public static long timeLeftMillis(Player p, String key) {
        return Optional.ofNullable(data.get(p.getUniqueId()))
                .map(m -> m.getOrDefault(key, 0L) - System.currentTimeMillis())
                .orElse(0L);
    }

    public static boolean reset(Player p, String key, long seconds) {
        return resetMillis(p, key, seconds * 1000);
    }

    public static boolean resetMillis(Player p, String key, long millis) {
        data.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>())
                .put(key, System.currentTimeMillis() + millis);
        return true;
    }

    public static boolean has(Player p, String key) {
        return timeLeftMillis(p, key) > 0;
    }

    public static void clear(Player p, String key) {
        Map<String, Long> map = data.get(p.getUniqueId());
        if (map != null) map.remove(key);
    }

    public static void clearAll(Player p) {
        data.remove(p.getUniqueId());
    }

    public static void extend(Player p, String key, long seconds) {
        data.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>())
                .merge(key, System.currentTimeMillis() + seconds * 1000, Math::max);
    }

    public static String getFormatted(Player p, String key) {
        long millis = timeLeftMillis(p, key);
        if (millis <= 0) return "0s";
        return String.format(Locale.US, "%.1fs", millis / 1000.0);
    }
}