package com.flame.api.gui;

import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public final class GuiElement {

    private final ItemStack item;
    private final Consumer<GuiClick> onClick;

    private boolean cancelClick = true;
    private boolean closeOnClick = false;

    private GuiElement(ItemStack item, Consumer<GuiClick> onClick) {
        this.item = item;
        this.onClick = onClick;
    }

    public static GuiElement of(ItemStack item) {
        return new GuiElement(item, null);
    }

    public static GuiElement button(ItemStack item, Consumer<GuiClick> onClick) {
        return new GuiElement(item, onClick);
    }

    public ItemStack item() {
        return item;
    }

    public boolean cancelClick() {
        return cancelClick;
    }

    public GuiElement cancelClick(boolean cancel) {
        this.cancelClick = cancel;
        return this;
    }

    public boolean closeOnClick() {
        return closeOnClick;
    }

    public GuiElement closeOnClick(boolean close) {
        this.closeOnClick = close;
        return this;
    }

    void click(GuiClick click) {
        if (onClick != null) onClick.accept(click);
    }
}
