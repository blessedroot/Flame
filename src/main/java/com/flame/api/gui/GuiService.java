package com.flame.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public final class GuiService implements Listener {

    private static GuiService instance;

    public static GuiService init(Plugin plugin) {
        if (instance != null) return instance;
        instance = new GuiService(plugin);
        return instance;
    }

    public static GuiService get() {
        if (instance == null) {
            throw new IllegalStateException("GuiService plaki-plaki");
        }
        return instance;
    }

    private final Plugin plugin;
    private final Map<UUID, GuiSession> sessions = new ConcurrentHashMap<>();

    private GuiService(Plugin plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public Plugin plugin() {
        return plugin;
    }

    public void open(Player player, GuiMenu menu) {
        close(player);

        GuiSession session = new GuiSession(this, player, menu);
        sessions.put(player.getUniqueId(), session);

        Inventory inv = Bukkit.createInventory(new GuiHolder(session.id()), menu.size(), menu.title());
        session.attach(inv);

        menu.render(session);
        session.flushToInventory();

        player.openInventory(inv);

        session.startAutoRefresh();
    }

    public void refresh(Player player) {
        GuiSession session = sessions.get(player.getUniqueId());
        if (session != null) session.refresh();
    }

    public void close(Player player) {
        GuiSession existing = sessions.remove(player.getUniqueId());
        if (existing != null) {
            existing.shutdown(false);
            if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null) {
                if (player.getOpenInventory().getTopInventory().getHolder() instanceof GuiHolder) {
                    player.closeInventory();
                }
            }
        }
    }

    public void shutdownAll() {
        for (GuiSession s : sessions.values()) {
            s.shutdown(false);
        }
        sessions.clear();
    }

    private GuiSession fromTop(Inventory inv) {
        if (inv == null) return null;
        if (!(inv.getHolder() instanceof GuiHolder)) return null;
        GuiHolder holder = (GuiHolder) inv.getHolder();
        for (GuiSession s : sessions.values()) {
            if (s.id().equals(holder.sessionId())) return s;
        }
        return null;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onClick(InventoryClickEvent e) {
        InventoryView view = e.getView();
        Inventory top = view.getTopInventory();

        GuiSession session = fromTop(top);
        if (session == null) return;

        int rawSlot = e.getRawSlot();
        boolean topInvClick = rawSlot >= 0 && rawSlot < top.getSize();

        if (session.menu().cancelAllClicks()) {
            e.setCancelled(true);
        }

        if (!topInvClick) {
            if (!session.menu().allowPlayerInventoryClicks()) {
                e.setCancelled(true);
            }
            return;
        }

        int slot = rawSlot;

        GuiElement element = session.element(slot);
        if (element == null) return;

        if (element.cancelClick()) {
            e.setCancelled(true);
        }

        GuiClick click = new GuiClick(session, e);

        try {
            element.click(click);
        } catch (Throwable t) {
            plugin.getLogger().severe("Exception in click handler (bam bam bam bam bam): " + t.getMessage());
            t.printStackTrace();
        }

        if (element.closeOnClick()) {
            click.close();
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDrag(InventoryDragEvent e) {
        Inventory top = e.getWhoClicked().getOpenInventory().getTopInventory();
        GuiSession session = fromTop(top);
        if (session == null) return;

        if (session.menu().cancelAllClicks()) {
            e.setCancelled(true);
            return;
        }

        for (int rawSlot : e.getRawSlots()) {
            if (rawSlot < top.getSize()) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent e) {
        Inventory top = e.getInventory();
        GuiSession session = fromTop(top);
        if (session == null) return;

        sessions.remove(session.player().getUniqueId());
        session.shutdown(true);
    }
}
