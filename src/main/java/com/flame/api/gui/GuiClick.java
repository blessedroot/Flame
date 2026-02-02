package com.flame.api.gui;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * author : s0ckett
 * date : 01.02.26
 */
public final class GuiClick {

    private final GuiSession session;
    private final InventoryClickEvent event;

    GuiClick(GuiSession session, InventoryClickEvent event) {
        this.session = session;
        this.event = event;
    }

    public GuiSession session() { return session; }
    public GuiMenu menu() { return session.menu(); }
    public Player player() { return (Player) event.getWhoClicked(); }

    public int slot() { return event.getSlot(); }
    public ClickType type() { return event.getClick(); }

    public boolean isLeft() { return event.isLeftClick(); }
    public boolean isRight() { return event.isRightClick(); }
    public boolean isShift() { return event.isShiftClick(); }

    public void cancel() { event.setCancelled(true); }
    public void allow() { event.setCancelled(false); }

    public void close() {
        player().closeInventory();
    }

    public void refresh() {
        session.refresh();
    }
}
