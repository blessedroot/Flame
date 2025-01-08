package com.flame.api.gui;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class PaginatedGui implements InventoryHolder {

    /**
     * author : s0ckett
     * date : 08.01.25
     */

    private final Inventory inventory;
    private final int size;
    private final String title;
    private int currentPage = 0;
    private final List<List<String>> pages = new ArrayList<>();

    public PaginatedGui(String title, int size) {
        this.title = title;
        this.size = size;
        this.inventory = Bukkit.createInventory(this, size, title);
    }

    public void addPage(List<String> items) {
        pages.add(items);
    }

    public void open(Player player) {
        updatePage();
        player.openInventory(inventory);
    }

    public void nextPage() {
        if (currentPage < pages.size() - 1) {
            currentPage++;
            updatePage();
        }
    }

    public void previousPage() {
        if (currentPage > 0) {
            currentPage--;
            updatePage();
        }
    }

    private void updatePage() {
        inventory.clear();
        List<String> items = pages.get(currentPage);
        for (int i = 0; i < items.size(); i++) {
            inventory.setItem(i, createItem(items.get(i)));
        }
    }

    public abstract void handleClick(InventoryClickEvent event);

    private ItemStack createItem(String name) {
        return null;
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}