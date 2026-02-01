package com.flame.api.item.manager;

import com.flame.api.FlameAPIPlugin;
import com.flame.api.item.Item;
import com.flame.api.item.util.NbtItemTag;
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

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : s0ckett
 * date : 01.02.26
 */
public class ItemManager implements Listener {

    private static final Logger LOGGER = Logger.getLogger(ItemManager.class.getName());

    private final FlameAPIPlugin plugin;

    private final Map<UUID, Item> items = new ConcurrentHashMap<>();

    private final Map<UUID, Set<UUID>> playerItems = new ConcurrentHashMap<>();

    private boolean handleInventoryClicks = true;
    private boolean handlePlayerInteract = true;

    public ItemManager(FlameAPIPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public UUID registerItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        UUID uuid = UUID.randomUUID();
        items.put(uuid, item);
        return uuid;
    }

    public boolean unregisterItem(UUID uuid) {
        if (uuid == null) return false;
        Item removed = items.remove(uuid);
        if (removed == null) return false;

        playerItems.values().forEach(set -> set.remove(uuid));
        return true;
    }

    public Item getItem(UUID uuid) {
        return uuid == null ? null : items.get(uuid);
    }

    public boolean isRegistered(ItemStack itemStack) {
        return getUuid(itemStack) != null;
    }

    public UUID addItemToPlayer(Player player, Item item, int slot) {
        if (player == null || item == null) {
            throw new IllegalArgumentException("Player and item cannot be null");
        }

        UUID itemUuid = registerItem(item);

        ItemStack stack = item.getItemStack();
        stack = NbtItemTag.withFlameId(stack, itemUuid.toString());

        Inventory inventory = player.getInventory();
        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, stack);
        } else {
            inventory.addItem(stack);
        }

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

        for (ItemStack stack : player.getInventory().getContents()) {
            UUID found = getUuid(stack);
            if (uuid.equals(found)) {
                player.getInventory().remove(stack);
            }
        }

        Set<UUID> playerItemSet = playerItems.get(player.getUniqueId());
        if (playerItemSet != null) {
            playerItemSet.remove(uuid);
        }

        return true;
    }

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

    public void clearPlayerItems(Player player) {
        if (player == null) {
            return;
        }

        Set<UUID> uuids = playerItems.remove(player.getUniqueId());
        if (uuids == null || uuids.isEmpty()) {
            return;
        }

        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            UUID found = getUuid(contents[i]);
            if (found != null && uuids.contains(found)) {
                contents[i] = null;
            }
        }
        player.getInventory().setContents(contents);
    }

    public int getItemCount() {
        return items.size();
    }

    public int getPlayerCount() {
        return playerItems.size();
    }

    public void clearAll() {
        items.clear();
        playerItems.clear();
    }

    public void setHandleInventoryClicks(boolean handle) {
        this.handleInventoryClicks = handle;
    }

    public void setHandlePlayerInteract(boolean handle) {
        this.handlePlayerInteract = handle;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!handleInventoryClicks) {
            return;
        }

        ItemStack clicked = event.getCurrentItem();
        UUID uuid = getUuid(clicked);
        if (uuid == null) {
            return;
        }

        Item item = items.get(uuid);
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

        ItemStack stack = event.getItem();
        UUID uuid = getUuid(stack);
        if (uuid == null) {
            return;
        }

        Item item = items.get(uuid);
        if (item == null || !item.hasAction()) {
            return;
        }

        event.setCancelled(true);
        Player player = event.getPlayer();

        try {
            item.executeAction(player);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "Error executing item action for " + player.getName(), ex);
            player.sendMessage("§cОшибка при использовании предмета");
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerItems.remove(event.getPlayer().getUniqueId());
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

    private static UUID getUuid(ItemStack stack) {
        if (stack == null) return null;
        String id = NbtItemTag.readFlameId(stack);
        if (id == null) return null;
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }
}
