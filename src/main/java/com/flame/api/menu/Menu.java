package com.flame.api.menu;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;

import java.util.*;
import java.util.function.Consumer;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class Menu {
    private final Inventory inv;
    private final Map<Integer, Consumer<Player>> actions = new HashMap<>();

    public Menu(String title, int rows) {
        inv = Bukkit.createInventory(null, rows*9, title);
    }

    public void setItem(int slot, ItemStack item, Consumer<Player> act) {
        inv.setItem(slot, item);
        actions.put(slot, act);
    }

    public Inventory build() { return inv; }
    public Optional<Consumer<Player>> getAction(int slot) { return Optional.ofNullable(actions.get(slot)); }
}
