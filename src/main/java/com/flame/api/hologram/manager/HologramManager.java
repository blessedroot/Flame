package com.flame.api.hologram.manager;

import com.flame.api.hologram.Hologram;
import com.flame.api.hologram.impl.HologramImpl;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HologramManager {

    /**
     * author : s0ckett
     * date : 08.01.25
     */

    private static HologramManager instance;
    private final Map<UUID, Hologram> holograms = new HashMap<>();

    private HologramManager() {}

    public static HologramManager getInstance() {
        if (instance == null) {
            instance = new HologramManager();
        }
        return instance;
    }

    public Hologram createHologram(Location location, String text) {
        Hologram hologram = new HologramImpl(location, text);
        holograms.put(hologram.getId(), hologram);
        return hologram;
    }

    public void deleteHologram(UUID id) {
        Hologram hologram = holograms.remove(id);
        if (hologram != null) {
            hologram.delete();
        }
    }

    public void clearAll() {
        holograms.values().forEach(Hologram::delete);
        holograms.clear();
    }
}