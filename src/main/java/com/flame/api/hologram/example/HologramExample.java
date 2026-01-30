package com.flame.api.hologram.example;

import com.flame.api.hologram.Hologram;
import com.flame.api.hologram.manager.HologramManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

/**
 * author : s0ckett
 * date : 30.01.26
 *
 * Пример
 */
public class HologramExample {

    private final HologramManager manager = HologramManager.getInstance();

    /**
     * как же это простецко!
     */
    public void createSimpleHologram(Location location) {
        Hologram hologram = manager.createHologram(location, "§aHello World!");

        hologram.setText("§bNew Text");
        hologram.setVisibility(false);
        hologram.delete();
    }

    /**
     * чевоооооооооооооо
     */
    public void createMultilineHologram(Location location) {
        List<String> lines = Arrays.asList(
                "§6§lserver",
                "§aonline: §f100",
                "§eqqqqq"
        );

        Hologram hologram = manager.createHologram(location, lines);

        // Обновление всех строк
        hologram.setLines(Arrays.asList(
                "§6§lserver",
                "§aonline: §f150",
                "§eqqqqq"
        ));
    }

    /**
     * Поиск голограмм рядом с игроком
     */
    public void findNearbyHolograms(Player player) {
        Location playerLoc = player.getLocation();

        List<Hologram> nearby = manager.getHologramsInRange(playerLoc, 10.0);

        player.sendMessage("§aholo neeeeeear" + nearby.size());

        nearby.forEach(hologram -> hologram.setVisibility(false));
    }

    /**
     * Очистка голограмм в мире
     */
    public void cleanupWorld(Player player) {
        manager.deleteHologramsInWorld(player.getWorld());
        // manager.clearAll();
    }

    /**
     * Безопасная работа с голограммами
     */
    public void safeHologramUsage(Player player) {
        Hologram hologram = manager.createHologram(player.getLocation(), "test");

        // Проверка перед использованием
        if (!hologram.isDeleted()) {
            hologram.setText("Голограмма уже активна!!!");
        }

        // Удаление через менеджер
        manager.deleteHologram(hologram.getId());

        // Попытка использовать удалённую голограмму выбросит исключение
        try {
            hologram.setText("Это не сработает, друг");
        } catch (IllegalStateException e) {
            player.sendMessage("§cГолограмма уже удалена!!!");
        }
    }

    /**
     * получить статистику? легко
     */
    public void getStatistics(Player player) {
        int total = manager.getHologramCount();
        int inWorld = manager.getHologramsInWorld(player.getWorld()).size();

        player.sendMessage("§eВсего голограмм: §f" + total);
        player.sendMessage("§eВ текущем мире: §f" + inWorld);
    }

    /**
     * периодическая очистка удалённых голограмм
     * (вызывай ток в таймере раз в 5-10 минут)
     */
    public void periodicCleanup() {
        manager.cleanupDeletedHolograms();
    }
}
