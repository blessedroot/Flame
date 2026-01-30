package com.flame.api.effect;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * author : s0ckett
 * date : 31.01.25
 */
public class Effect {

    public static void playParticle(Player player, EnumParticle particle, Location location, int count) {
        playParticle(player, particle, location, count, 0f, 0f, 0f, 0f);
    }

    public static void playParticle(Player player, EnumParticle particle, Location location,
                                    int count, float offsetX, float offsetY, float offsetZ, float speed) {
        if (player == null || particle == null || location == null) {
            return;
        }

        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                particle,
                true,
                (float) location.getX(),
                (float) location.getY(),
                (float) location.getZ(),
                offsetX, offsetY, offsetZ,
                speed,
                count
        );

        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void playParticleForAll(Location location, EnumParticle particle, int count, double radius) {
        playParticleForAll(location, particle, count, 0f, 0f, 0f, 0f, radius);
    }

    public static void playParticleForAll(Location location, EnumParticle particle, int count,
                                          float offsetX, float offsetY, float offsetZ, float speed, double radius) {
        if (location == null || location.getWorld() == null) {
            return;
        }

        Collection<Player> nearbyPlayers = location.getWorld().getNearbyEntities(location, radius, radius, radius)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());

        for (Player player : nearbyPlayers) {
            playParticle(player, particle, location, count, offsetX, offsetY, offsetZ, speed);
        }
    }

    public static void playEffect(Player player, EnumParticle particle, Location location, int count, Sound sound) {
        playParticle(player, particle, location, count);
        playSound(player, sound);
    }

    public static void playEffectForAll(Location location, EnumParticle particle, int count, Sound sound, double radius) {
        playParticleForAll(location, particle, count, radius);
        playSoundForAll(location, sound, radius);
    }

    public static void playSound(Player player, Sound sound) {
        playSound(player, sound, 1f, 1f);
    }

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        if (player != null && sound != null) {
            player.playSound(player.getLocation(), sound, volume, pitch);
        }
    }

    public static void playSoundForAll(Location location, Sound sound, double radius) {
        playSoundForAll(location, sound, 1f, 1f, radius);
    }

    public static void playSoundForAll(Location location, Sound sound, float volume, float pitch, double radius) {
        if (location == null || location.getWorld() == null || sound == null) {
            return;
        }

        Collection<Player> nearbyPlayers = location.getWorld().getNearbyEntities(location, radius, radius, radius)
                .stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());

        for (Player player : nearbyPlayers) {
            player.playSound(location, sound, volume, pitch);
        }
    }

    public static void playCircle(Player player, EnumParticle particle, Location center, int points, double radius) {
        if (player == null || particle == null || center == null) {
            return;
        }

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Location loc = center.clone().add(x, 0, z);
            playParticle(player, particle, loc, 1);
        }
    }

    public static void playCircleForAll(Location center, EnumParticle particle, int points,
                                        double radius, double viewRadius) {
        if (center == null || center.getWorld() == null) {
            return;
        }

        for (int i = 0; i < points; i++) {
            double angle = 2 * Math.PI * i / points;
            double x = radius * Math.cos(angle);
            double z = radius * Math.sin(angle);
            Location loc = center.clone().add(x, 0, z);
            playParticleForAll(loc, particle, 1, viewRadius);
        }
    }

    public static void playSphere(Player player, EnumParticle particle, Location center, int points, double radius) {
        if (player == null || particle == null || center == null) {
            return;
        }

        double increment = (2 * Math.PI) / points;

        for (double phi = 0; phi < Math.PI; phi += increment) {
            for (double theta = 0; theta < 2 * Math.PI; theta += increment) {
                double x = radius * Math.sin(phi) * Math.cos(theta);
                double y = radius * Math.cos(phi);
                double z = radius * Math.sin(phi) * Math.sin(theta);

                Location loc = center.clone().add(x, y, z);
                playParticle(player, particle, loc, 1);
            }
        }
    }


    public static void playLine(Player player, EnumParticle particle, Location start, Location end, int points) {
        if (player == null || particle == null || start == null || end == null) {
            return;
        }

        Vector direction = end.toVector().subtract(start.toVector());
        double length = direction.length();
        direction.normalize();

        for (int i = 0; i < points; i++) {
            double distance = (length / points) * i;
            Location loc = start.clone().add(direction.clone().multiply(distance));
            playParticle(player, particle, loc, 1);
        }
    }

    public static void playSpiral(Player player, EnumParticle particle, Location center,
                                  double radius, double height, int points) {
        if (player == null || particle == null || center == null) {
            return;
        }

        for (int i = 0; i < points; i++) {
            double angle = (4 * Math.PI * i) / points;
            double currentRadius = (radius * i) / points;
            double y = (height * i) / points;

            double x = currentRadius * Math.cos(angle);
            double z = currentRadius * Math.sin(angle);

            Location loc = center.clone().add(x, y, z);
            playParticle(player, particle, loc, 1);
        }
    }

    public static void playCube(Player player, EnumParticle particle, Location center, double size, int pointsPerEdge) {
        if (player == null || particle == null || center == null) {
            return;
        }

        double half = size / 2;

        drawEdge(player, particle, center.clone().add(-half, -half, -half), center.clone().add(half, -half, -half), pointsPerEdge);
        drawEdge(player, particle, center.clone().add(half, -half, -half), center.clone().add(half, -half, half), pointsPerEdge);
        drawEdge(player, particle, center.clone().add(half, -half, half), center.clone().add(-half, -half, half), pointsPerEdge);
        drawEdge(player, particle, center.clone().add(-half, -half, half), center.clone().add(-half, -half, -half), pointsPerEdge);

        drawEdge(player, particle, center.clone().add(-half, half, -half), center.clone().add(half, half, -half), pointsPerEdge);
        drawEdge(player, particle, center.clone().add(half, half, -half), center.clone().add(half, half, half), pointsPerEdge);
        drawEdge(player, particle, center.clone().add(half, half, half), center.clone().add(-half, half, half), pointsPerEdge);
        drawEdge(player, particle, center.clone().add(-half, half, half), center.clone().add(-half, half, -half), pointsPerEdge);

        drawEdge(player, particle, center.clone().add(-half, -half, -half), center.clone().add(-half, half, -half), pointsPerEdge);
        drawEdge(player, particle, center.clone().add(half, -half, -half), center.clone().add(half, half, -half), pointsPerEdge);
        drawEdge(player, particle, center.clone().add(half, -half, half), center.clone().add(half, half, half), pointsPerEdge);
        drawEdge(player, particle, center.clone().add(-half, -half, half), center.clone().add(-half, half, half), pointsPerEdge);
    }

    private static void drawEdge(Player player, EnumParticle particle, Location start, Location end, int points) {
        playLine(player, particle, start, end, points);
    }

    public static void playExplosion(Player player, EnumParticle particle, Location center, double maxRadius, int steps) {
        if (player == null || particle == null || center == null) {
            return;
        }

        for (int step = 1; step <= steps; step++) {
            double currentRadius = (maxRadius * step) / steps;
            int points = (int) (20 * currentRadius);
            playCircle(player, particle, center, points, currentRadius);
        }
    }

    public static void playLevelUpEffect(Player player) {
        playEffect(player, EnumParticle.VILLAGER_HAPPY, player.getLocation().add(0, 1, 0), 20, Sound.LEVEL_UP);
    }

    public static void playDeathEffect(Player player) {
        playEffect(player, EnumParticle.SMOKE_NORMAL, player.getLocation().add(0, 1, 0), 30, Sound.WITHER_DEATH);
    }

    public static void playTeleportEffect(Location location) {
        playParticleForAll(location, EnumParticle.PORTAL, 50, 0.5f, 1f, 0.5f, 1f, 30);
        playSoundForAll(location, Sound.ENDERMAN_TELEPORT, 1f, 1f, 30);
    }

    public static void playHealEffect(Player player) {
        Location loc = player.getLocation().add(0, 1, 0);
        playParticle(player, EnumParticle.HEART, loc, 10, 0.5f, 0.5f, 0.5f, 0f);
        playSound(player, Sound.LEVEL_UP, 1f, 2f);
    }

    public static void playCriticalEffect(Location location) {
        playParticleForAll(location, EnumParticle.CRIT, 15, 0.3f, 0.3f, 0.3f, 0.5f, 20);
        playSoundForAll(location, Sound.SUCCESSFUL_HIT, 1f, 0.8f, 20);
    }

    public static void playMagicEffect(Location location) {
        playParticleForAll(location, EnumParticle.SPELL_WITCH, 20, 0.5f, 0.5f, 0.5f, 0.5f, 25);
        playSoundForAll(location, Sound.LEVEL_UP, 1f, 1.5f, 25);
    }

    public static void playFireEffect(Location location) {
        playParticleForAll(location, EnumParticle.FLAME, 30, 0.3f, 0.3f, 0.3f, 0.1f, 20);
        playSoundForAll(location, Sound.FIRE, 1f, 1f, 20);
    }

    public static void playSmokeEffect(Location location) {
        playParticleForAll(location, EnumParticle.SMOKE_LARGE, 20, 0.5f, 0.5f, 0.5f, 0.1f, 20);
    }

    public static void playExplosionEffect(Location location) {
        playParticleForAll(location, EnumParticle.EXPLOSION_HUGE, 1, 0f, 0f, 0f, 0f, 50);
        playSoundForAll(location, Sound.EXPLODE, 2f, 0.8f, 50);
    }

    public static void playWaterEffect(Location location) {
        playParticleForAll(location, EnumParticle.WATER_SPLASH, 30, 0.5f, 0.5f, 0.5f, 0.3f, 20);
        playSoundForAll(location, Sound.SPLASH, 1f, 1f, 20);
    }

    public static void playLoveEffect(Player player) {
        Location loc = player.getLocation().add(0, 2, 0);
        playParticle(player, EnumParticle.HEART, loc, 5, 0.5f, 0.5f, 0.5f, 0f);
    }

    public static void playAngryEffect(Player player) {
        Location loc = player.getLocation().add(0, 2.5, 0);
        playParticle(player, EnumParticle.VILLAGER_ANGRY, loc, 5, 0.3f, 0.3f, 0.3f, 0f);
    }

    public static void playNoteEffect(Location location) {
        playParticleForAll(location, EnumParticle.NOTE, 10, 0.5f, 0.5f, 0.5f, 1f, 20);
    }
}