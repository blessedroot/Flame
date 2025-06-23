package com.flame.api.npc.event;

import com.flame.api.npc.model.FlameNpc;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class NpcClickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    private final Player player;
    private final FlameNpc npc;
    private final ClickType clickType;

    public enum ClickType {
        LEFT, RIGHT
    }

    public NpcClickEvent(Player player, FlameNpc npc, ClickType clickType) {
        this.player = player;
        this.npc = npc;
        this.clickType = clickType;
    }

    public Player getPlayer() {
        return player;
    }

    public FlameNpc getNpc() {
        return npc;
    }

    public ClickType getClickType() {
        return clickType;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
