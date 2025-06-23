package com.flame.api.chain;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Queue;
import java.util.function.Consumer;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class Chain {

    private final Player player;
    private final Queue<Consumer<Player>> steps = new LinkedList<>();

    public Chain(Player player) {
        this.player = player;
    }

    public static Chain start(Player player) {
        return new Chain(player);
    }

    public Chain then(Consumer<Player> step) {
        steps.add(step);
        return this;
    }

    public void runSync() {
        Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("FlameAPI"), this::runNext);
    }

    public void runAsync() {
        Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("FlameAPI"), this::runNext);
    }

    private void runNext() {
        Consumer<Player> step = steps.poll();
        if (step != null) {
            step.accept(player);
            runNext();
        }
    }
}
