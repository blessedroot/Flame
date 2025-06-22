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
     * date : 09.01.25
     */

    private static FlameAPIPlugin instance;
    private FlameAPI flameAPI;
    private NpcManager npcManager;
    private WebServer webServer;

    public static FlameAPIPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerAPI();
        ProtocolLibrary.getProtocolManager().addPacketListener(new NpcClickListener());
        startWebServer();
        getLogger().info("Вызываю пожарных! Включение");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(player -> flameAPI.getHologramManager().clearAll());
        stopWebServer();
        getLogger().info("Отзываю пожарных! Выключение");
    }

    private void registerAPI() {
        flameAPI = new FlameAPI(
                HologramManager.getInstance(),
                new ScoreboardManager(),
                new ItemManager(),
                new NpcManager()
        );
    }

    private void startWebServer() {
        webServer = new WebServer();
        webServer.start();
    }

    private void stopWebServer() {
        if (webServer != null) webServer.stop();
    }

    public FlameAPI getFlameAPI() {
        return flameAPI;
    }
}
