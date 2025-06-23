package com.flame.api.cooldown;

import org.bukkit.entity.Player;
import java.util.*;

public class CooldownManager {
    private static final Map<UUID, Map<String, Long>> data = new HashMap<>();

    public static boolean check(Player p, String key, long seconds) {
        return timeLeft(p, key) <= 0 || reset(p, key, seconds);
    }

    public static long timeLeft(Player p, String key) {
        return Optional.ofNullable(data.get(p.getUniqueId()))
                .map(m->m.getOrDefault(key, 0L)-System.currentTimeMillis())
                .orElse(0L) / 1000;
    }

    public static boolean reset(Player p, String key, long seconds) {
        data.computeIfAbsent(p.getUniqueId(), k->new HashMap<>())
                .put(key, System.currentTimeMillis()+seconds*1000);
        return true;
    }

    public static void clear(Player p, String key) {
        data.getOrDefault(p.getUniqueId(), Collections.emptyMap()).remove(key);
    }
}
