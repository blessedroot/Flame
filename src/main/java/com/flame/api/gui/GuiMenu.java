package com.flame.api.gui;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public class GuiMenu {

    private final String title;
    private final int rows;

    private boolean cancelAllClicks = true;
    private boolean allowPlayerInventoryClicks = false;

    private ItemStack background;
    private int autoRefreshTicks = 0;

    private final List<Consumer<GuiSession>> renderers = new ArrayList<>();
    private final List<Consumer<GuiClose>> closeHandlers = new ArrayList<>();

    public GuiMenu(String title, int rows) {
        if (rows < 1 || rows > 6) throw new IllegalArgumentException("rows must be 1..6");
        this.title = title;
        this.rows = rows;
    }

    public String title() { return title; }
    public int rows() { return rows; }
    public int size() { return rows * 9; }

    public boolean cancelAllClicks() { return cancelAllClicks; }
    public GuiMenu cancelAllClicks(boolean cancel) { this.cancelAllClicks = cancel; return this; }

    public boolean allowPlayerInventoryClicks() { return allowPlayerInventoryClicks; }
    public GuiMenu allowPlayerInventoryClicks(boolean allow) { this.allowPlayerInventoryClicks = allow; return this; }

    public ItemStack background() { return background; }

    public GuiMenu fill(ItemStack item) { this.background = item; return this; }

    public GuiMenu autoRefresh(int ticks) { this.autoRefreshTicks = ticks; return this; }

    public int autoRefreshTicks() { return autoRefreshTicks; }

    public GuiMenu set(int slot, GuiElement element) {
        render(session -> session.putElement(slot, element));
        return this;
    }

    public GuiMenu grid(Consumer<GuiGrid> gridFn) {
        render(session -> gridFn.accept(new GuiGrid(session)));
        return this;
    }

    public GuiMenu render(Consumer<GuiSession> renderer) {
        this.renderers.add(renderer);
        return this;
    }

    public GuiMenu onClose(Consumer<GuiClose> handler) {
        this.closeHandlers.add(handler);
        return this;
    }

    void render(GuiSession session) {
        session.clear();
        for (Consumer<GuiSession> r : renderers) r.accept(session);
    }

    void fireClose(GuiClose close) {
        for (Consumer<GuiClose> h : closeHandlers) h.accept(close);
    }

    public static GuiMenu of(String title, int rows) {
        return new GuiMenu(title, rows);
    }

    public static GuiElement button(ItemStack item, Consumer<GuiClick> onClick) {
        return GuiElement.button(item, onClick);
    }
}
