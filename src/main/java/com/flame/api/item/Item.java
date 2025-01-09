package com.flame.api.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Item {

    /**
     * Получить ItemStack предмета.
     *
     * @return ItemStack
     */
    ItemStack getItemStack();

    /**
     * Установить действие, которое выполняется при нажатии на предмет.
     *
     * @param action Действие
     */
    void setClickAction(PlayerAction action);

    /**
     * Выполнить действие, связанное с предметом.
     *
     * @param player Игрок, который взаимодействовал с предметом
     */
    void executeAction(Player player);

    /**
     * Функциональный интерфейс для действий при клике.
     */
    @FunctionalInterface
    interface PlayerAction {
        void execute(Player player);
    }
}