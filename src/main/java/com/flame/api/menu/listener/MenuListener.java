package com.flame.api.menu.listener;


import com.flame.api.menu.holder.MenuManagerHolder;
import com.flame.api.menu.manager.MenuManager;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.entity.Player;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class MenuListener implements Listener {

    private final MenuManager menuManager;

    public MenuListener(MenuManager menuManager) {
        this.menuManager = menuManager;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p)) return;
        var menu = MenuManagerHolder.get().getMenu(p.getOpenInventory().getTitle());
        if (menu==null) return;
        e.setCancelled(true);
        menu.getAction(e.getRawSlot()).ifPresent(act->act.accept(p));
    }
}
