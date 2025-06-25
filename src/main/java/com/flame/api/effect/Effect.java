package com.flame.api.effect;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class Effect {

    public static void playParticle(Player player, EnumParticle particle, Location loc, int count) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                particle,
                true,
                (float) loc.getX(),
                (float) loc.getY(),
                (float) loc.getZ(),
                0f, 0f, 0f,
                0f,
                count
        );
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void playCircle(Player player, EnumParticle particle, Location center, int points, double radius) {
        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Location loc = center.clone().add(x, 0, z);
            playParticle(player, particle, loc, 1);
        }
    }

    public static void playSound(Player player, Sound sound) {
        player.playSound(player.getLocation(), sound, 1f, 1f);
    }

    public static void playLevelUpEffect(Player player) {
        playEffect(player, EnumParticle.VILLAGER_HAPPY, player.getLocation(), 10, Sound.LEVEL_UP);
    }

    public static void playDeathEffect(Player player) {
        playEffect(player, EnumParticle.SMOKE_NORMAL, player.getLocation(), 20, Sound.WITHER_DEATH);
    }
}