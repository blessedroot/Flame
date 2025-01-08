package com.flame.api;

import com.flame.api.api.FlameAPI;
import com.flame.api.hologram.manager.HologramManager;
import com.flame.api.scoreboard.manager.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FlameAPIPlugin extends JavaPlugin {

    /**
     * author : s0ckett
     * date : 09.01.25
     */

    private static FlameAPIPlugin instance;
    private FlameAPI flameAPI;

    /**
     * Получить экз.плагина.
     */
    public static FlameAPIPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerAPI();
        getLogger().info("Вызываю пожарных! Включение");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            flameAPI.getHologramManager().clearAll();
        });

        getLogger().info("Отзываю пожарных! Выключение");
    }

    /**
     * Регистрация апишечки.
     */
    private void registerAPI() {
        HologramManager hologramManager = HologramManager.getInstance();
        ScoreboardManager scoreboardManager = new ScoreboardManager();

        flameAPI = new FlameAPI(hologramManager, scoreboardManager);
    }

    /**
     * Получить экз.апишечки.
     */
    public FlameAPI getFlameAPI() {
        return flameAPI;
    }
}