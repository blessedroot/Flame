package com.flame.api.api;

import com.flame.api.config.ConfigManager;
import com.flame.api.discord.manager.DiscordManager;
import com.flame.api.gui.GuiService;
import com.flame.api.hologram.manager.HologramManager;
import com.flame.api.item.manager.ItemManager;
import com.flame.api.npc.manager.NpcManager;
import com.flame.api.scoreboard.manager.ScoreboardManager;

public class FlameAPI {

    /**
     * author : s0ckett
     * date : 02.02.26
     */

    private final HologramManager hologramManager;
    private final ScoreboardManager scoreboardManager;
    private final ItemManager itemManager;
    private final NpcManager npcManager;
    private final DiscordManager discordManager;
    private final ConfigManager configManager;
    private final GuiService guiService;

    public FlameAPI(HologramManager hologramManager,
                    ScoreboardManager scoreboardManager,
                    ItemManager itemManager,
                    NpcManager npcManager,
                    DiscordManager discordManager,
                    ConfigManager configManager,
                    GuiService guiService)
    {

        this.hologramManager = hologramManager;
        this.scoreboardManager = scoreboardManager;
        this.itemManager = itemManager;
        this.npcManager = npcManager;
        this.discordManager = discordManager;
        this.configManager = configManager;
        this.guiService = guiService;
    }


    /**
     * Получить менеджер для работы с голограммами.
     *
     * @return HologramManager
     */
    public HologramManager getHologramManager() {
        return hologramManager;
    }

    /**
     * Получить менеджер для работы со скорбордами.
     *
     * @return ScoreboardManager
     */
    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    /**
     * Получить менеджер для работы с айтемами.
     *
     * @return ItemManager
     */

    public ItemManager getItemManager() {
        return itemManager;
    }

    /**
     * Получить менеджер для работы с NPC.
     *
     * @return NpcManager
     */

    public NpcManager getNpcManager() {
        return npcManager;
    }

    /**
     * Получить менеджер для работы с Discord.
     *
     * @return DiscordManager
     */
    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Получить сервис для работы с GUI.
     *
     * @return GuiService
     */
    public GuiService getGuiService() {
        return guiService;
    }
}
