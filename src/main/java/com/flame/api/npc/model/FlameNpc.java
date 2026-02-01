package com.flame.api.npc.model;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.flame.api.npc.util.FlameNpcPacketUtil;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * author : s0ckett
 * date : 23.06.25
 */


public class FlameNpc {

    private final UUID uuid;
    private final String name;
    private final Location location;
    private final WrappedGameProfile profile;
    private final boolean lookAtPlayer;

    public FlameNpc(String name, Location location, WrappedGameProfile profile, boolean lookAtPlayer) {
        this.uuid = UUID.randomUUID();
        this.name = name;
        this.location = location;
        this.profile = profile;
        this.lookAtPlayer = lookAtPlayer;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public WrappedGameProfile getProfile() {
        return profile;
    }

    public boolean shouldLookAtPlayer() {
        return lookAtPlayer;
    }

    public void spawn(Player viewer) {
        FlameNpcPacketUtil.spawnNpc(viewer, this);
    }

    public void destroy(Player viewer) {
        FlameNpcPacketUtil.destroyNpc(viewer, this);
    }
}
