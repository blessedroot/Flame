package com.flame.api.gui;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.UUID;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public final class GuiHolder implements InventoryHolder {

    private final UUID sessionId;

    public GuiHolder(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public UUID sessionId() {
        return sessionId;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
