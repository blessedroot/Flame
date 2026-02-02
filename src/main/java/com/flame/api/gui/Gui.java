package com.flame.api.gui;

import org.bukkit.entity.Player;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public final class Gui {

    private Gui() {}

    public static GuiMenu menu(String title, int rows) {
        return new GuiMenu(title, rows);
    }

    public static GuiPagedMenu paged(String title, int rows) {
        return new GuiPagedMenu(title, rows);
    }

    public static void open(Player player, GuiMenu menu) {
        GuiService.get().open(player, menu);
    }

    public static void close(Player player) {
        GuiService.get().close(player);
    }
}
