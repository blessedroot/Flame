package com.flame.api.npc.builder;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.flame.api.npc.fetcher.Skin;
import com.flame.api.npc.model.FlameNpc;
import org.bukkit.Location;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class NpcBuilder {

    private String name;
    private String skin;
    private Location location;
    private boolean lookAtPlayer = false;

    public NpcBuilder name(String name) {
        this.name = name;
        return this;
    }

    public NpcBuilder skin(String skin) {
        this.skin = skin;
        return this;
    }

    public NpcBuilder location(Location location) {
        this.location = location;
        return this;
    }

    public NpcBuilder lookAtPlayer(boolean lookAtPlayer) {
        this.lookAtPlayer = lookAtPlayer;
        return this;
    }

    public FlameNpc build() {
        WrappedGameProfile profile = Skin.fetchSkin(skin);
        return new FlameNpc(name, location, profile, lookAtPlayer);
    }
}
