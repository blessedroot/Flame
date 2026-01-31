package com.flame.api;

import com.comphenix.protocol.ProtocolLibrary;
import com.flame.api.api.FlameAPI;
import com.flame.api.hologram.manager.HologramManager;
import com.flame.api.item.manager.ItemManager;
import com.flame.api.npc.listener.NpcClickListener;
import com.flame.api.npc.manager.NpcManager;
import com.flame.api.scoreboard.manager.ScoreboardManager;
import com.flame.api.web.server.WebServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FlameAPIPlugin extends JavaPlugin {

    /**
     * author : s0ckett
     * date : 23.06.25
     */

    private static FlameAPIPlugin instance;
    private FlameAPI flameAPI;
    private WebServer webServer;
    public FlameAPIPlugin plugin;

    public static FlameAPIPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerAPI();
//        getCommand("menu").setExecutor(new MenuCommand());
        ProtocolLibrary.getProtocolManager().addPacketListener(new NpcClickListener());
        getLogger().info("Вызываю пожарных! Включение");
        startWebServer(plugin);
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> flameAPI.getHologramManager().clearAll());
        stopWebServer();
//        MenuManagerHolder.reset();
        getLogger().info("Отзываю пожарных! Выключение");
    }

    private void registerAPI() {
        HologramManager hologramManager = HologramManager.getInstance();
        ScoreboardManager scoreboardManager = new ScoreboardManager();
        ItemManager itemManager = new ItemManager();
        NpcManager npcManager = new NpcManager();
//        MenuManager menuManager = new MenuManager(this);

//        MenuManagerHolder.set(menuManager);
//        getServer().getPluginManager().registerEvents(new MenuListener(menuManager), this);

        flameAPI = new FlameAPI(
                hologramManager,
                scoreboardManager,
                itemManager,
                npcManager
        );
    }

    private void startWebServer(FlameAPIPlugin plugin) {
        webServer = new WebServer(plugin);
        webServer.start();
    }

    private void stopWebServer() {
        if (webServer != null) webServer.stop();
    }

    public FlameAPI getFlameAPI() {
        return flameAPI;
    }
}