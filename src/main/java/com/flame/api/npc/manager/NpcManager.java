package com.flame.api.npc.manager;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.flame.api.npc.fetcher.Skin;
import com.flame.api.npc.model.FlameNpc;
import org.bukkit.Location;

import java.util.*;

public class NpcManager {

    private final Map<UUID, FlameNpc> npcs = new HashMap<>();

    public FlameNpc create(String name, Location location, String skinOwner) {
        return create(name, location, skinOwner, false);
    }

    public FlameNpc create(String name, Location location, String skinOwner, boolean lookAtPlayer) {
        WrappedGameProfile profile = Skin.fetchSkin(skinOwner);
        FlameNpc npc = new FlameNpc(name, location, profile, lookAtPlayer);
        npcs.put(npc.getUuid(), npc);
        return npc;
    }

    public Collection<FlameNpc> getAll() {
        return npcs.values();
    }

    public void remove(FlameNpc npc) {
        npcs.remove(npc.getUuid());
    }
}
