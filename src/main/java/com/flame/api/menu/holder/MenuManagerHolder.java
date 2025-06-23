package com.flame.api.menu.holder;

import com.flame.api.menu.manager.MenuManager;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class MenuManagerHolder {

    private static MenuManager instance;

    /**
     * Установить текущий MenuManager.
     */
    public static void set(MenuManager manager) {
        instance = manager;
    }

    /**
     * Получить текущий MenuManager или выбросить исключение, если он не установлен.
     */
    public static MenuManager get() {
        if (instance == null) {
            throw new IllegalStateException("Менюшка не горит! Вызови MenuManagerHolder.set(...) перед использованием.");
        }
        return instance;
    }

    /**
     * Проверка: установлен ли MenuManager.
     *
     * @return true, если instance не null
     */
    public static boolean isRegistered() {
        return instance != null;
    }

    /**
     * Сброс MenuManager (можно вызывать при onDisable).
     */
    public static void reset() {
        instance = null;
    }
}
