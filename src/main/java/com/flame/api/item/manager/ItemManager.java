package com.flame.api.item.manager;

import com.flame.api.FlameAPIPlugin;
import com.flame.api.item.Item;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : s0ckett
 * date : 31.01.26
 */
public class ItemManager implements Listener {

    private static final Logger LOGGER = Logger.getLogger(ItemManager.class.getName());

    private final Map<UUID, Item> items = new ConcurrentHashMap<>();

    private final Map<ItemStack, UUID> itemStackToUuid = new ConcurrentHashMap<>();

    private final Map<UUID, Set<UUID>> playerItems = new ConcurrentHashMap<>();

    public FlameAPIPlugin flameAPIPlugin;
    private boolean handleInventoryClicks = true;
    private boolean handlePlayerInteract = true;

    public ItemManager() {
        if (flameAPIPlugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        Bukkit.getPluginManager().registerEvents(this, flameAPIPlugin);
    }

    public UUID registerItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }

        UUID uuid = UUID.randomUUID();
        items.put(uuid, item);
        itemStackToUuid.put(item.getItemStack(), uuid);

        return uuid;
    }

    public boolean unregisterItem(UUID uuid) {
        Item item = items.remove(uuid);
        if (item != null) {
            itemStackToUuid.remove(item.getItemStack());

            // Удаляем из всех игроков
            playerItems.values().forEach(set -> set.remove(uuid));

            return true;
        }
        return false;
    }

    public Item getItem(UUID uuid) {
        return items.get(uuid);
    }

    public Item getItem(ItemStack itemStack) {
        if (itemStack == null) {
            return null;
        }

        UUID uuid = itemStackToUuid.get(itemStack);
        return uuid != null ? items.get(uuid) : null;
    }

    public boolean isRegistered(ItemStack itemStack) {
        return getItem(itemStack) != null;
    }

    public UUID addItemToPlayer(Player player, Item item, int slot) {
        if (player == null || item == null) {
            throw new IllegalArgumentException("Player and item cannot be null");
        }

        UUID itemUuid = registerItem(item);
        Inventory inventory = player.getInventory();

        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item.getItemStack());
        } else {
            inventory.addItem(item.getItemStack());
        }

        // Отслеживаем предметы игрока
        playerItems.computeIfAbsent(player.getUniqueId(), k -> ConcurrentHashMap.newKeySet())
                .add(itemUuid);

        return itemUuid;
    }

    public UUID addItemToPlayer(Player player, Item item) {
        return addItemToPlayer(player, item, -1);
    }

    public boolean removeItemFromPlayer(Player player, UUID uuid) {
        if (player == null || uuid == null) {
            return false;
        }

        Item item = items.get(uuid);
        if (item == null) {
            return false;
        }

        player.getInventory().remove(item.getItemStack());

        Set<UUID> playerItemSet = playerItems.get(player.getUniqueId());
        if (playerItemSet != null) {
            playerItemSet.remove(uuid);
        }

        return true;
    }

    /**
     * гет всех предметов игрока
     * @param player игрок
     * @return неизменяемый список предметов
     */
    public List<Item> getPlayerItems(Player player) {
        if (player == null) {
            return Collections.emptyList();
        }

        Set<UUID> uuids = playerItems.get(player.getUniqueId());
        if (uuids == null || uuids.isEmpty()) {
            return Collections.emptyList();
        }

        List<Item> result = new ArrayList<>();
        for (UUID uuid : uuids) {
            Item item = items.get(uuid);
            if (item != null) {
                result.add(item);
            }
        }

        return Collections.unmodifiableList(result);
    }

    /**
     * clearplayeritems
     * @param player игрок
     */
    public void clearPlayerItems(Player player) {
        if (player == null) {
            return;
        }

        Set<UUID> uuids = playerItems.remove(player.getUniqueId());
        if (uuids != null) {
            for (UUID uuid : uuids) {
                Item item = items.get(uuid);
                if (item != null) {
                    player.getInventory().remove(item.getItemStack());
                }
            }
        }
    }

    /**
     * посмотреть количество зарегистрированных предметов
     * @return количество предметов
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * посмотреть количество игроков с предметами
     * @return количество игроков
     */
    public int getPlayerCount() {
        return playerItems.size();
    }

    /**
     * clear
     */
    public void clearAll() {
        items.clear();
        itemStackToUuid.clear();
        playerItems.clear();
    }

    /**
     * on/off обработку кликов в инвентаре
     * @param handle true для включения
     */
    public void setHandleInventoryClicks(boolean handle) {
        this.handleInventoryClicks = handle;
    }

    /**
     * on/off обработку взаимодействий игрока
     * @param handle true для включения
     */
    public void setHandlePlayerInteract(boolean handle) {
        this.handlePlayerInteract = handle;
    }

    /**
     * clicks
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!handleInventoryClicks) {
            return;
        }

        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType().toString().equals("AIR")) {
            return;
        }

        Item item = getItem(clickedItem);
        if (item == null || !item.hasAction()) {
            return;
        }

        event.setCancelled(true);

        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            try {
                item.executeAction(player);
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Error executing item action for " + player.getName(), ex);
                player.sendMessage("§cОшибка при использовании предмета");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!handlePlayerInteract) {
            return;
        }

        ItemStack item = event.getItem();
        if (item == null || item.getType().toString().equals("AIR")) {
            return;
        }

        Item registeredItem = getItem(item);
        if (registeredItem == null || !registeredItem.hasAction()) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();

        try {
            registeredItem.executeAction(player);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error executing item action for " + player.getName(), ex);
            player.sendMessage("§cОшибка при использовании предмета");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // удаляем отслеживание предметов игрока, ну эт ситуативно, оставил ниже
        // playerItems.remove(player.getUniqueId());
    }


    /**
     * чекнуть обрабатываются ли клики в инвентаре
     * @return true если обрабатываются
     */
    public boolean isHandlingInventoryClicks() {
        return handleInventoryClicks;
    }

    /**
     * чекнуть обрабатываются ли взаимодействия игрока
     * @return true если обрабатываются
     */
    public boolean isHandlingPlayerInteract() {
        return handlePlayerInteract;
    }
}