package com.flame.api.item;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * author : s0ckett
 * date : 31.01.26
 */
public interface Item {

    /**
     * Получить ItemStack предмета
     * @return ItemStack
     */
    ItemStack getItemStack();

    /**
     * Получить материал предмета
     * @return Material
     */
    Material getMaterial();

    /**
     * Получить название предмета
     * @return название
     */
    String getDisplayName();

    /**
     * Получить описание предмета
     * @return список строк описания
     */
    List<String> getLore();

    /**
     * Установить действие при клике
     * @param action действие
     */
    void setClickAction(PlayerAction action);

    /**
     * Получить действие при клике
     * @return действие или null
     */
    PlayerAction getClickAction();

    /**
     * Выполнить действие, связанное с предметом
     * @param player игрок
     */
    void executeAction(Player player);

    /**
     * Проверить, имеет ли предмет действие
     * @return true если действие установлено
     */
    boolean hasAction();

    /**
     * Клонировать предмет
     * @return новый экземпляр предмета
     */
    Item clone();

    /**
     * Установить количество предметов
     * @param amount количество
     */
    void setAmount(int amount);

    /**
     * Получить количество предметов
     * @return количество
     */
    int getAmount();

    /**
     * Функциональный интерфейс для действий при клике
     */
    @FunctionalInterface
    interface PlayerAction {
        void execute(Player player);
    }
}