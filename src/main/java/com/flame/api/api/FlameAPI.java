package com.flame.api.api;

import com.flame.api.hologram.manager.HologramManager;
import com.flame.api.item.manager.ItemManager;
import com.flame.api.npc.manager.NpcManager;
import com.flame.api.scoreboard.manager.ScoreboardManager;

public class FlameAPI {

    /**
     * author : s0ckett
     * date : 09.01.25
     */

    private final HologramManager hologramManager;
    private final ScoreboardManager scoreboardManager;
    private final ItemManager itemManager;
    private final NpcManager npcManager;

    public FlameAPI(HologramManager hologramManager, ScoreboardManager scoreboardManager, ItemManager itemManager, NpcManager npcManager) {
        this.hologramManager = HologramManager.getInstance();
        this.scoreboardManager = new ScoreboardManager();
        this.itemManager = itemManager;
        this.npcManager = npcManager;
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
}
