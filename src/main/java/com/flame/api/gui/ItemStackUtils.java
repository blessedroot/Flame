package com.flame.api.gui;

import org.bukkit.inventory.ItemStack;

/**
 * author : s0ckett
 * date : 10.06.25
 */

final class ItemStackUtils {
    private ItemStackUtils() {}

    static boolean same(ItemStack a, ItemStack b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a.getAmount() != b.getAmount()) return false;
        return a.isSimilar(b);
    }
}
