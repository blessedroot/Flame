//package com.flame.api.chain;
//
//import org.bukkit.Bukkit;
//import org.bukkit.entity.Player;
//
//import java.util.LinkedList;
//import java.util.Queue;
//import java.util.function.Consumer;
//
///**
// * author : s0ckett
// * date : 23.06.25
// */
//
//public class Chain {
//
//    private final Player player;
//    private final Queue<Runnable> steps = new LinkedList<>();
//    private boolean async = false;
//    private boolean cancelled = false;
//
//    public Chain(Player player) {
//        this.player = player;
//    }
//
//    public static Chain start(Player player) {
//        return new Chain(player);
//    }
//
//    public Chain then(Consumer<Player> step) {
//        steps.add(() -> {
//            if (!cancelled) step.accept(player);
//            runNext();
//        });
//        return this;
//    }
//
//    public Chain delay(long ticks) {
//        steps.add(() -> {
//            if (cancelled) return;
//            if (async) {
//                Bukkit.getScheduler().runTaskLaterAsynchronously(Bukkit.getPluginManager().getPlugin("FlameAPI"), this::runNext, ticks);
//            } else {
//                Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("FlameAPI"), this::runNext, ticks);
//            }
//        });
//        return this;
//    }
//
//    public Chain async() {
//        this.async = true;
//        return this;
//    }
//
//    public Chain sync() {
//        this.async = false;
//        return this;
//    }
//
//    public void cancel() {
//        this.cancelled = true;
//    }
//
//    public void runSync() {
//        this.async = false;
//        runNext();
//    }
//
//    public void runAsync() {
//        this.async = true;
//        runNext();
//    }
//
//    private void runNext() {
//        Runnable step = steps.poll();
//        if (step == null || cancelled) return;
//        if (async) {
//            Bukkit.getScheduler().runTaskAsynchronously(Bukkit.getPluginManager().getPlugin("FlameAPI"), step);
//        } else {
//            Bukkit.getScheduler().runTask(Bukkit.getPluginManager().getPlugin("FlameAPI"), step);
//        }
//    }
//}