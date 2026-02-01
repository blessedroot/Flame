package com.flame.api;

import com.comphenix.protocol.ProtocolLibrary;
import com.flame.api.api.FlameAPI;
import com.flame.api.config.ConfigManager;
import com.flame.api.discord.manager.DiscordManager;
import com.flame.api.hologram.manager.HologramManager;
import com.flame.api.item.manager.ItemManager;
import com.flame.api.npc.listener.NpcClickListener;
import com.flame.api.npc.manager.NpcManager;
import com.flame.api.scoreboard.manager.ScoreboardManager;
import com.flame.api.web.server.NettyWebServer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FlameAPIPlugin extends JavaPlugin {

    /**
     * author : s0ckett
     * date : 01.02.26
     */

    private static FlameAPIPlugin instance;
    private FlameAPI flameAPI;
    private NettyWebServer webServer;

    public static FlameAPIPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        registerAPI();

        ProtocolLibrary.getProtocolManager().addPacketListener(new NpcClickListener());

        getLogger().info("Вызываю пожарных!");
        startWebServer();
    }

    @Override
    public void onDisable() {
        if (flameAPI != null) {
            flameAPI.getHologramManager().clearAll();
            flameAPI.getScoreboardManager().shutdown();
            flameAPI.getItemManager().clearAll();
            flameAPI.getNpcManager().shutdown();
        }
        stopWebServer();
        getLogger().info("Отзываю пожарных! Пожарные в ахуе!");
    }

    private void registerAPI() {
        HologramManager hologramManager = HologramManager.getInstance();
        ScoreboardManager scoreboardManager = new ScoreboardManager(this);
        ItemManager itemManager = new ItemManager(this);
        NpcManager npcManager = new NpcManager(this);
        DiscordManager discordManager = new DiscordManager();
        ConfigManager configManager = new ConfigManager(this);

        flameAPI = new FlameAPI(
                hologramManager,
                scoreboardManager,
                itemManager,
                npcManager,
                discordManager,
                configManager
        );
    }

    private void startWebServer() {
        webServer = new NettyWebServer(this);
        webServer.start();
    }

    private void stopWebServer() {
        if (webServer != null) webServer.stop();
    }

    public FlameAPI getFlameAPI() {
        return flameAPI;
    }
}