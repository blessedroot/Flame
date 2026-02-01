package com.flame.api.npc.manager;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.flame.api.FlameAPIPlugin;
import com.flame.api.npc.fetcher.Skin;
import com.flame.api.npc.model.FlameNpc;
import com.flame.api.npc.util.FlameNpcPacketUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public class NpcManager {

    private final FlameAPIPlugin plugin;
    private final Map<UUID, FlameNpc> npcs = new ConcurrentHashMap<>();
    private BukkitTask lookTask;

    public NpcManager(FlameAPIPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.plugin = plugin;
        startLookTask();
    }

    public FlameNpc create(String name, Location location, String skinOwner) {
        return create(name, location, skinOwner, false);
    }

    public FlameNpc create(String name, Location location, String skinOwner, boolean lookAtPlayer) {
        WrappedGameProfile profile = Skin.fetchSkin(skinOwner);
        FlameNpc npc = new FlameNpc(name, location, profile, lookAtPlayer);
        npcs.put(npc.getUuid(), npc);
        return npc;
    }

    public Optional<FlameNpc> get(UUID uuid) {
        return Optional.ofNullable(npcs.get(uuid));
    }

    public Collection<FlameNpc> getAll() {
        return Collections.unmodifiableCollection(npcs.values());
    }

    public void spawnTo(Player viewer) {
        if (viewer == null) return;
        for (FlameNpc npc : npcs.values()) {
            npc.spawn(viewer);
        }
    }

    public void destroyFrom(Player viewer) {
        if (viewer == null) return;
        for (FlameNpc npc : npcs.values()) {
            npc.destroy(viewer);
        }
    }

    public void remove(FlameNpc npc) {
        if (npc == null) return;
        npcs.remove(npc.getUuid());
        for (Player viewer : Bukkit.getOnlinePlayers()) {
            npc.destroy(viewer);
        }
    }

    private void startLookTask() {
        this.lookTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();
            if (players.isEmpty()) return;

            for (FlameNpc npc : npcs.values()) {
                if (!npc.shouldLookAtPlayer()) continue;
                for (Player viewer : players) {
                    FlameNpcPacketUtil.lookAtPlayer(viewer, npc);
                }
            }
        }, 10L, 10L);
    }

    public void shutdown() {
        if (lookTask != null) {
            lookTask.cancel();
            lookTask = null;
        }

        for (Player viewer : Bukkit.getOnlinePlayers()) {
            destroyFrom(viewer);
        }

        npcs.clear();
    }
}
