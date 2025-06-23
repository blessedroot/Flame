package com.flame.api.npc.manager;

import com.flame.api.npc.event.NpcClickEvent;
import com.flame.api.npc.model.FlameNpc;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * author : s0ckett
 * date : 23.06.25
 */


public class NpcClickManager {

    private static final Map<UUID, BiConsumer<Player, NpcClickEvent.ClickType>> actions = new HashMap<>();

    public static void onClick(FlameNpc npc, BiConsumer<Player, NpcClickEvent.ClickType> action) {
        actions.put(npc.getUuid(), action);
    }

    public static void handleClick(Player player, FlameNpc npc, NpcClickEvent.ClickType type) {
        BiConsumer<Player, NpcClickEvent.ClickType> action = actions.get(npc.getUuid());
        if (action != null) {
            action.accept(player, type);
        }
    }
}
