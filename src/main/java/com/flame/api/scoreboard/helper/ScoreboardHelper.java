package com.flame.api.scoreboard.helper;

import org.bukkit.entity.Player;

import java.util.List;

/**
 * author : s0ckett
 * date : 31.01.26
 */
public interface ScoreboardHelper {

    /**
     * Установить заголовок скорборда
     */
    void setTitle(String title);

    /**
     * Получить текущий заголовок
     */
    String getTitle();

    /**
     * Установить строку скорборда
     */
    void setLine(int line, String content);

    /**
     * Установить все строки сразу
     */
    void setLines(List<String> lines);

    /**
     * Получить содержимое строки
     */
    String getLine(int line);

    /**
     * Получить все строки
     */
    java.util.Map<Integer, String> getLines();

    /**
     * Удалить строку
     */
    void removeLine(int line);

    /**
     * Установить пустую строку
     */
    void setEmptyLine(int line);

    /**
     * Обновить отображение скорборда
     */
    void update();

    /**
     * Показать скорборд игроку
     */
    void showTo(Player player);

    /**
     * Скрыть скорборд у игрока
     */
    void hideFrom(Player player);

    /**
     * Показать скорборд всем игрокам
     */
    void broadcast();

    /**
     * Очистить скорборд
     */
    void clear();

    /**
     * Проверить показан ли скорборд игроку
     */
    boolean isShownTo(Player player);

    /**
     * Получить количество строк
     */
    int getLineCount();
}