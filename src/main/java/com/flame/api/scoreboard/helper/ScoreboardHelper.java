package com.flame.api.scoreboard.helper;

import org.bukkit.entity.Player;

public interface ScoreboardHelper {

    /**
     * author : s0ckett
     * date : 09.01.25
     */

    void setTitle(String title); // Установить заголовок скорборда
    void setLine(int line, String content); // Установить строку
    void removeLine(int line); // Удалить строку
    void update(); // Обновить отображение скорборда
    void showTo(Player player); // Показать скорборд игроку
    void broadcast(); // Показать скорборд всем игрокам
    void clear(); // Очистить скорборд
    void setEmptyLine(int line); // Установить строку, в которой пустой текст (заместо пустых ковычек)
}
