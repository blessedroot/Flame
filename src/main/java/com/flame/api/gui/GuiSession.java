package com.flame.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public final class GuiSession {

    private final GuiService service;
    private final UUID id = UUID.randomUUID();
    private final Player player;
    private final GuiMenu menu;

    private Inventory inventory;

    private final Map<Integer, GuiElement> elements = new HashMap<>();
    private final Map<Integer, ItemStack> lastRendered = new HashMap<>();
    private final Map<String, Object> state = new HashMap<>();

    private BukkitTask refreshTask;

    GuiSession(GuiService service, Player player, GuiMenu menu) {
        this.service = service;
        this.player = player;
        this.menu = menu;
    }

    public UUID id() { return id; }
    public Player player() { return player; }
    public GuiMenu menu() { return menu; }
    public GuiService service() { return service; }

    void attach(Inventory inv) {
        this.inventory = inv;
    }

    public Inventory inventory() { return inventory; }

    public GuiState state() { return new GuiState(state); }

    void putElement(int slot, GuiElement element) {
        if (slot < 0 || slot >= menu.size()) return;
        if (element == null) elements.remove(slot);
        else elements.put(slot, element);
    }

    public GuiElement element(int slot) {
        return elements.get(slot);
    }

    public void clear() {
        elements.clear();
    }

    public void refresh() {
        menu.render(this);
        flushToInventory();
    }

    void flushToInventory() {
        if (inventory == null) return;

        ItemStack background = menu.background();
        if (background != null) {
            for (int i = 0; i < inventory.getSize(); i++) {
                if (!elements.containsKey(i)) {
                    inventory.setItem(i, background.clone());
                }
            }
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            GuiElement el = elements.get(i);
            ItemStack next = (el != null) ? el.item() : null;

            if (next == null && background != null && !elements.containsKey(i)) continue;

            ItemStack prev = lastRendered.get(i);
            boolean changed = !ItemStackUtils.same(prev, next);

            if (changed) {
                inventory.setItem(i, next == null ? null : next.clone());
                if (next == null) lastRendered.remove(i);
                else lastRendered.put(i, next.clone());
            }
        }
    }

    void startAutoRefresh() {
        int ticks = menu.autoRefreshTicks();
        if (ticks <= 0) return;

        refreshTask = service.plugin().getServer().getScheduler().runTaskTimer(
                service.plugin(),
                this::refresh,
                ticks,
                ticks
        );
    }

    void shutdown(boolean fireCloseHandlers) {
        if (refreshTask != null) {
            refreshTask.cancel();
            refreshTask = null;
        }
        if (fireCloseHandlers) {
            try {
                menu.fireClose(new GuiClose(this));
            } catch (Throwable t) {
                service.plugin().getLogger().severe("Exception in close handler (plaki-plaki): " + t.getMessage());
                t.printStackTrace();
            }
        }
        elements.clear();
        lastRendered.clear();
        state.clear();
        inventory = null;
    }
}
