package com.flame.api.item.impl;

import com.flame.api.item.Item;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemImpl implements Item {

    private final ItemStack itemStack;
    private Item.PlayerAction clickAction;

    /**
     * Конструктор для создания предмета.
     *
     * @param material   Материал предмета
     * @param title      Название предмета
     * @param description Список строк для описания предмета
     */
    public ItemImpl(Material material, String title, List<String> description) {
        this.itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(title);
            meta.setLore(description);
            itemStack.setItemMeta(meta);
        }
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public void setClickAction(Item.PlayerAction action) {
        this.clickAction = action;
    }

    @Override
    public void executeAction(Player player) {
        if (clickAction != null) {
            clickAction.execute(player);
        }
    }
}