package com.flame.api.hologram.manager;

import com.flame.api.hologram.Hologram;
import com.flame.api.hologram.impl.HologramImpl;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * author : s0ckett
 * date : 30.01.26
 */
public class HologramManager {

    private static volatile HologramManager instance;

    private final Map<UUID, Hologram> holograms;

    private HologramManager() {
        this.holograms = new ConcurrentHashMap<>();
    }

    public static HologramManager getInstance() {
        if (instance == null) {
            synchronized (HologramManager.class) {
                if (instance == null) {
                    instance = new HologramManager();
                }
            }
        }
        return instance;
    }

    public Hologram createHologram(Location location, String text) {
        Hologram hologram = new HologramImpl(location, text);
        holograms.put(hologram.getId(), hologram);
        return hologram;
    }

    public Hologram createHologram(Location location, List<String> lines) {
        Hologram hologram = new HologramImpl(location, lines);
        holograms.put(hologram.getId(), hologram);
        return hologram;
    }

    public Optional<Hologram> getHologram(UUID id) {
        return Optional.ofNullable(holograms.get(id));
    }

    public Collection<Hologram> getAllHolograms() {
        return Collections.unmodifiableCollection(holograms.values());
    }

    public List<Hologram> getHologramsInWorld(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        return holograms.values().stream()
                .filter(hologram -> !hologram.isDeleted())
                .filter(hologram -> hologram.getLocation().getWorld().equals(world))
                .collect(Collectors.toList());
    }

    public List<Hologram> getHologramsInRange(Location center, double radius) {
        if (center == null || center.getWorld() == null) {
            throw new IllegalArgumentException("Location and world cannot be null");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be positive");
        }

        double radiusSquared = radius * radius;

        return holograms.values().stream()
                .filter(hologram -> !hologram.isDeleted())
                .filter(hologram -> hologram.getLocation().getWorld().equals(center.getWorld()))
                .filter(hologram -> hologram.getLocation().distanceSquared(center) <= radiusSquared)
                .collect(Collectors.toList());
    }

    public void deleteHologram(UUID id) {
        Hologram hologram = holograms.remove(id);
        if (hologram != null && !hologram.isDeleted()) {
            hologram.delete();
        }
    }

    public void deleteHologramsInWorld(World world) {
        if (world == null) {
            throw new IllegalArgumentException("World cannot be null");
        }

        holograms.entrySet().removeIf(entry -> {
            Hologram hologram = entry.getValue();
            if (hologram.getLocation().getWorld().equals(world)) {
                if (!hologram.isDeleted()) {
                    hologram.delete();
                }
                return true;
            }
            return false;
        });
    }

    public void clearAll() {
        holograms.values().forEach(hologram -> {
            if (!hologram.isDeleted()) {
                hologram.delete();
            }
        });
        holograms.clear();
    }

    public int getHologramCount() {
        return (int) holograms.values().stream()
                .filter(hologram -> !hologram.isDeleted())
                .count();
    }

    public void cleanupDeletedHolograms() {
        holograms.entrySet().removeIf(entry -> entry.getValue().isDeleted());
    }
}