package com.flame.api.gui;

import org.bukkit.entity.Player;

/**
 * author : s0ckett
 * date : 01.02.26
 */
public final class GuiClose {

    private final GuiSession session;

    GuiClose(GuiSession session) {
        this.session = session;
    }

    public GuiSession session() { return session; }
    public Player player() { return session.player(); }
    public GuiMenu menu() { return session.menu(); }
}
