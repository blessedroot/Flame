package com.flame.api.npc.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.*;
import com.flame.api.npc.model.FlameNpc;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.EnumSet;

/**
 * author : s0ckett
 * date : 23.06.25
 */


public class FlameNpcPacketUtil {

    public static void spawnNpc(Player player, FlameNpc npc) {
        Location loc = npc.getLocation();
        WrappedGameProfile profile = npc.getProfile();

        int entityId = npc.getUuid().hashCode();

        PacketContainer playerInfo = new PacketContainer(PacketType.Play.Server.PLAYER_INFO);
        playerInfo.getPlayerInfoActions().write(0, EnumSet.of(EnumWrappers.PlayerInfoAction.ADD_PLAYER));
        playerInfo.getPlayerInfoDataLists().write(0,
                Collections.singletonList(new PlayerInfoData(profile, 0, EnumWrappers.NativeGameMode.SURVIVAL,
                        WrappedChatComponent.fromText(npc.getName()))));

        PacketContainer namedSpawn = new PacketContainer(PacketType.Play.Server.NAMED_ENTITY_SPAWN);
        namedSpawn.getIntegers().write(0, entityId);
        namedSpawn.getUUIDs().write(0, npc.getUuid());
        namedSpawn.getDoubles().write(0, loc.getX());
        namedSpawn.getDoubles().write(1, loc.getY());
        namedSpawn.getDoubles().write(2, loc.getZ());
        namedSpawn.getBytes().write(0, (byte) ((loc.getYaw() * 256.0f) / 360.0f));
        namedSpawn.getBytes().write(1, (byte) ((loc.getPitch() * 256.0f) / 360.0f));

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, playerInfo);
        ProtocolLibrary.getProtocolManager().sendServerPacket(player, namedSpawn);
    }

    public static void destroyNpc(Player player, FlameNpc npc) {
        int entityId = npc.getUuid().hashCode();

        PacketContainer destroy = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);
        destroy.getIntLists().write(0, Collections.singletonList(entityId));

        ProtocolLibrary.getProtocolManager().sendServerPacket(player, destroy);
    }

    public static void lookAtPlayer(Player target, FlameNpc npc) {
        int entityId = npc.getUuid().hashCode();
        Location from = npc.getLocation();
        Location to = target.getLocation();

        double dx = to.getX() - from.getX();
        double dy = to.getY() - from.getY();
        double dz = to.getZ() - from.getZ();

        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.toDegrees(Math.atan2(-dx, dz)));
        float pitch = (float) (Math.toDegrees(-Math.atan2(dy, distanceXZ)));

        PacketContainer headRotation = new PacketContainer(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        headRotation.getIntegers().write(0, entityId);
        headRotation.getBytes().write(0, (byte) ((yaw * 256.0f) / 360.0f));

        PacketContainer entityLook = new PacketContainer(PacketType.Play.Server.ENTITY_LOOK);
        entityLook.getIntegers().write(0, entityId);
        entityLook.getBytes().write(0, (byte) ((yaw * 256.0f) / 360.0f));
        entityLook.getBytes().write(1, (byte) ((pitch * 256.0f) / 360.0f));
        entityLook.getBooleans().write(0, true);
    }
}