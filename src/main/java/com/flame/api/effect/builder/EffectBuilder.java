package com.flame.api.effect.builder;

import com.flame.api.effect.Effect;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * author : s0ckett
 * date : 31.01.25
 */
public class EffectBuilder {

    private final Plugin plugin;
    private final List<EffectStep> steps = new ArrayList<>();
    private boolean repeat = false;
    private int repeatTimes = 1;
    private long repeatDelay = 0L;

    public EffectBuilder(Plugin plugin) {
        this.plugin = plugin;
    }

    public EffectBuilder addStep(long delay, Consumer<EffectContext> action) {
        steps.add(new EffectStep(delay, action));
        return this;
    }

    public EffectBuilder repeat(int times, long delay) {
        this.repeat = true;
        this.repeatTimes = times;
        this.repeatDelay = delay;
        return this;
    }

    public BukkitTask play(EffectContext context) {
        return new BukkitRunnable() {
            private int currentRepeat = 0;
            private int currentStep = 0;
            private long nextStepTime = 0;

            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();

                if (currentTime >= nextStepTime && currentStep < steps.size()) {
                    EffectStep step = steps.get(currentStep);

                    try {
                        step.action.accept(context);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    currentStep++;
                    if (currentStep < steps.size()) {
                        nextStepTime = currentTime + (steps.get(currentStep).delay * 50); // тики в мс
                    }
                }

                if (currentStep >= steps.size()) {
                    currentRepeat++;

                    if (repeat && currentRepeat < repeatTimes) {
                        currentStep = 0;
                        nextStepTime = currentTime + (repeatDelay * 50);
                    } else {
                        cancel();
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static class EffectContext {
        private final Player player;
        private final Location location;
        private final double radius;

        public EffectContext(Player player) {
            this.player = player;
            this.location = player.getLocation();
            this.radius = 30.0;
        }

        public EffectContext(Location location) {
            this.player = null;
            this.location = location;
            this.radius = 30.0;
        }

        public EffectContext(Location location, double radius) {
            this.player = null;
            this.location = location;
            this.radius = radius;
        }

        public Player getPlayer() {
            return player;
        }

        public Location getLocation() {
            return location;
        }

        public double getRadius() {
            return radius;
        }

        public boolean hasPlayer() {
            return player != null;
        }
    }

    private static class EffectStep {
        private final long delay;
        private final Consumer<EffectContext> action;

        EffectStep(long delay, Consumer<EffectContext> action) {
            this.delay = delay;
            this.action = action;
        }
    }

    public static BukkitTask createExplosionAnimation(Plugin plugin, Location location,
                                                      EnumParticle particle, int steps) {
        EffectBuilder builder = new EffectBuilder(plugin);

        for (int i = 1; i <= steps; i++) {
            final int step = i;
            builder.addStep(i, ctx -> {
                double radius = (3.0 * step) / steps;
                int points = (int) (20 * radius);
                Effect.playCircleForAll(location, particle, points, radius, 30);
            });
        }

        return builder.play(new EffectContext(location));
    }

    public static BukkitTask createSpiralAnimation(Plugin plugin, Location location,
                                                   EnumParticle particle, double height, int duration) {
        EffectBuilder builder = new EffectBuilder(plugin);

        for (int i = 0; i < duration; i++) {
            final int step = i;
            builder.addStep(1, ctx -> {
                double angle = (4 * Math.PI * step) / duration;
                double radius = 1.0;
                double y = (height * step) / duration;

                double x = radius * Math.cos(angle);
                double z = radius * Math.sin(angle);

                Location loc = location.clone().add(x, y, z);
                Effect.playParticleForAll(loc, particle, 1, 30);
            });
        }

        return builder.play(new EffectContext(location));
    }

    public static BukkitTask createPulseAnimation(Plugin plugin, Location location,
                                                  EnumParticle particle, int pulses) {
        EffectBuilder builder = new EffectBuilder(plugin);

        for (int pulse = 0; pulse < pulses; pulse++) {
            builder.addStep(pulse * 10L, ctx -> {
                Effect.playCircleForAll(location, particle, 30, 1.0, 30);
            });

            builder.addStep(pulse * 10L + 5L, ctx -> {
                Effect.playCircleForAll(location, particle, 40, 1.5, 30);
            });
        }

        return builder.play(new EffectContext(location));
    }

    public static BukkitTask createRotatingRingAnimation(Plugin plugin, Location location,
                                                         EnumParticle particle, int duration) {
        EffectBuilder builder = new EffectBuilder(plugin);

        for (int i = 0; i < duration; i++) {
            final int step = i;
            builder.addStep(1, ctx -> {
                double angle = (2 * Math.PI * step) / 20; // Поворот кольца
                double radius = 2.0;

                for (int j = 0; j < 20; j++) {
                    double particleAngle = (2 * Math.PI * j) / 20 + angle;
                    double x = radius * Math.cos(particleAngle);
                    double z = radius * Math.sin(particleAngle);

                    Location loc = location.clone().add(x, 1, z);
                    Effect.playParticleForAll(loc, particle, 1, 30);
                }
            });
        }

        return builder.play(new EffectContext(location));
    }

    public static BukkitTask createTornadoAnimation(Plugin plugin, Location location,
                                                    EnumParticle particle, double height, int duration) {
        EffectBuilder builder = new EffectBuilder(plugin);

        for (int i = 0; i < duration; i++) {
            final int step = i;
            builder.addStep(1, ctx -> {
                for (int j = 0; j < 5; j++) {
                    double angle = (8 * Math.PI * step) / duration + (2 * Math.PI * j / 5);
                    double y = (height * step) / duration;
                    double radius = 1.0 - (0.5 * step / duration);

                    double x = radius * Math.cos(angle);
                    double z = radius * Math.sin(angle);

                    Location loc = location.clone().add(x, y, z);
                    Effect.playParticleForAll(loc, particle, 1, 30);
                }
            });
        }

        return builder.play(new EffectContext(location));
    }

    public static BukkitTask createRainAnimation(Plugin plugin, Location location,
                                                 EnumParticle particle, double radius, int duration) {
        EffectBuilder builder = new EffectBuilder(plugin);

        for (int i = 0; i < duration; i++) {
            builder.addStep(2, ctx -> {
                for (int j = 0; j < 10; j++) {
                    double x = (Math.random() - 0.5) * 2 * radius;
                    double z = (Math.random() - 0.5) * 2 * radius;
                    double y = Math.random() * 3 + 2;

                    Location loc = location.clone().add(x, y, z);
                    Effect.playParticleForAll(loc, particle, 1, 0f, -0.5f, 0f, 0.1f, 30);
                }
            });
        }

        return builder.play(new EffectContext(location));
    }

    public static BukkitTask createFireworkAnimation(Plugin plugin, Location location) {
        EffectBuilder builder = new EffectBuilder(plugin);

        builder.addStep(0, ctx -> {
            Effect.playParticleForAll(location, EnumParticle.FIREWORKS_SPARK, 20, 0.1f, 2f, 0.1f, 0.3f, 50);
        });

        builder.addStep(20, ctx -> {
            Location explodeLocation = location.clone().add(0, 10, 0);
            Effect.playParticleForAll(explodeLocation, EnumParticle.FIREWORKS_SPARK, 100, 2f, 2f, 2f, 0.5f, 50);
            Effect.playSoundForAll(explodeLocation, Sound.FIREWORK_BLAST, 2f, 1f, 50);
        });

        return builder.play(new EffectContext(location));
    }
}