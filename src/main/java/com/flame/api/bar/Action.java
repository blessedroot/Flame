package com.flame.api.bar;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class Action {

    public static void sendBar(Player player, String message) {
        IChatBaseComponent chat = new ChatComponentText(message);
        PacketPlayOutChat packet = new PacketPlayOutChat(chat, (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        PacketPlayOutTitle packetTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn, stay, fadeOut);
        entityPlayer.playerConnection.sendPacket(packetTimes);

        if (title != null) {
            IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleComponent);
            entityPlayer.playerConnection.sendPacket(packetTitle);
        }

        if (subtitle != null) {
            IChatBaseComponent subtitleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle packetSubtitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, subtitleComponent);
            entityPlayer.playerConnection.sendPacket(packetSubtitle);
        }
    }

    public static void sendTitle(Player player, String title) {
        sendTitle(player, title, "", 10, 40, 10);
    }

    public static void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void broadcastBar(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            sendBar(player, message);
        }
    }
}
