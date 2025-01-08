package com.flame.api.api;

import com.flame.api.hologram.manager.HologramManager;
import com.flame.api.scoreboard.manager.ScoreboardManager;

public class FlameAPI {

    /**
     * author : s0ckett
     * date : 09.01.25
     */

    private final HologramManager hologramManager;
    private final ScoreboardManager scoreboardManager;

    public FlameAPI(HologramManager hologramManager, ScoreboardManager scoreboardManager) {
        this.hologramManager = HologramManager.getInstance();
        this.scoreboardManager = new ScoreboardManager();
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
}
