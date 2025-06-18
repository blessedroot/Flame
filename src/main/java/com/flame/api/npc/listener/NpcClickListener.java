package com.flame.api.npc.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.flame.api.FlameAPIPlugin;
import com.flame.api.npc.event.NpcClickEvent;
import com.flame.api.npc.manager.NpcClickManager;
import com.flame.api.npc.manager.NpcManager;
import com.flame.api.npc.model.FlameNpc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NpcClickListener extends PacketAdapter {

    public NpcClickListener() {
        super(FlameAPIPlugin.getInstance(), PacketType.Play.Client.USE_ENTITY);
    }

    @Override
    public void onPacketReceiving(PacketEvent event) {
        if (event.getPacketType() != PacketType.Play.Client.USE_ENTITY) return;

        Player player = event.getPlayer();
        int entityId = event.getPacket().getIntegers().read(0);

        EnumWrappers.EntityUseAction action = event.getPacket().getEnumEntityUseActions().read(0).getAction();

        NpcClickEvent.ClickType clickType = switch (action) {
            case INTERACT -> NpcClickEvent.ClickType.RIGHT;
            case ATTACK -> NpcClickEvent.ClickType.LEFT;
            default -> null;
        };

        if (clickType == null) return;

        NpcManager npcManager = FlameAPIPlugin.getInstance().getNpcManager();

        for (FlameNpc npc : npcManager.getAll()) {
            if (npc.getUuid().hashCode() == entityId) {
                Bukkit.getScheduler().runTask(FlameAPIPlugin.getInstance(), () ->
                        NpcClickManager.handleClick(player, npc, clickType));
                break;
            }
        }
    }
}

