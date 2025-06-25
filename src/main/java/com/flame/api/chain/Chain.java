package com.flame.api.chain;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class Chain {

    private final Player player;
    private final Plugin plugin;
    private final Queue<Runnable> steps = new LinkedList<>();
    private boolean async = false;
    private boolean cancelled = false;
    private BukkitTask currentTask;

    public Chain(Player player, Plugin plugin) {
        this.player = player;
        this.plugin = plugin;
    }

    public static Chain start(Player player) {
        return new Chain(player, Bukkit.getPluginManager().getPlugin("Flame"));
    }

    public Chain then(Consumer<Player> step) {
        steps.add(() -> {
            if (!cancelled) step.accept(player);
            runNext();
        });
        return this;
    }

    public Chain delay(long ticks) {
        steps.add(() -> {
            if (cancelled) return;
            currentTask = (async ?
                    Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, this::runNext, ticks) :
                    Bukkit.getScheduler().runTaskLater(plugin, this::runNext, ticks)
            );
        });
        return this;
    }

    public Chain async() {
        this.async = true;
        return this;
    }

    public Chain sync() {
        this.async = false;
        return this;
    }

    public void cancel() {
        this.cancelled = true;
        if (currentTask != null) currentTask.cancel();
    }

    public void run() {
        runNext();
    }

    private void runNext() {
        Runnable step = steps.poll();
        if (step == null || cancelled) return;
        if (async) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, step);
        } else {
            Bukkit.getScheduler().runTask(plugin, step);
        }
    }
}