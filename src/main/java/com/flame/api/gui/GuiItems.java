package com.flame.api.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public final class GuiItems {

    private GuiItems() {}

    public static ItemStack glass() {
        return named(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), " ");
    }

    public static ItemStack back(String name) {
        return named(new ItemStack(Material.ARROW), name);
    }

    public static ItemStack named(ItemStack item, String name, String... lore) {
        ItemStack it = item.clone();
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            if (lore != null && lore.length > 0) meta.setLore(Arrays.asList(lore));
            it.setItemMeta(meta);
        }
        return it;
    }

    public static ItemStack paper(String name, String... lore) {
        return named(new ItemStack(Material.PAPER), name, lore);
    }
}
