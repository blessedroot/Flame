package com.flame.api.gui;

import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * author : s0ckett
 * date : 30.01.26
 */
public final class GuiGrid {

    private final GuiSession session;

    GuiGrid(GuiSession session) {
        this.session = session;
    }

    public Cell at(int x, int y) {
        return new Cell(session, slot(x, y));
    }

    public void fillRect(int x1, int y1, int x2, int y2, ItemStack item) {
        for (int y = y1; y <= y2; y++) {
            for (int x = x1; x <= x2; x++) {
                int s = slot(x, y);
                session.putElement(s, GuiElement.of(item));
            }
        }
    }

    private int slot(int x, int y) {
        if (x < 0 || x > 8) throw new IllegalArgumentException("x должно быть 0..8.. ч..");
        if (y < 0 || y >= session.menu().rows()) throw new IllegalArgumentException("y должно быть 0.. друн.." + (session.menu().rows() - 1));
        return y * 9 + x;
    }

    public static final class Cell {
        private final GuiSession session;
        private final int slot;

        Cell(GuiSession session, int slot) {
            this.session = session;
            this.slot = slot;
        }

        public Cell item(ItemStack item) {
            session.putElement(slot, GuiElement.of(item));
            return this;
        }

        public Cell button(ItemStack item, Consumer<GuiClick> onClick) {
            session.putElement(slot, GuiElement.button(item, onClick));
            return this;
        }

        public Cell button(GuiElement element) {
            session.putElement(slot, element);
            return this;
        }
    }
}
