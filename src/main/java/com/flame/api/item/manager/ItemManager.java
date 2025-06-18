package com.flame.api.item.manager;

import com.flame.api.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class ItemManager implements Listener {

    private final Map<ItemStack, Item> items = new HashMap<>();

    /**
     * Конструктор менеджера. Регистрация событий.
     */
    public ItemManager() {
        Bukkit.getPluginManager().registerEvents(this, Bukkit.getPluginManager().getPlugin("FlameAPI"));
    }

    /**
     * Зарегистрировать предмет для управления.
     *
     * @param item Экземпляр предмета
     */
    public void registerItem(Item item) {
        items.put(item.getItemStack(), item);
    }

    /**
     * Добавить предмет в инвентарь игрока.
     *
     * @param player Игрок
     * @param item   Предмет
     * @param slot   Слот для размещения
     */
    public void addItemToPlayerInventory(Player player, Item item, int slot) {
        Inventory inventory = player.getInventory();
        inventory.setItem(slot, item.getItemStack());
        registerItem(item);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !items.containsKey(clickedItem)) return;

        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        Item item = items.get(clickedItem);
        if (item != null) {
            item.executeAction(player);
        }
    }
}