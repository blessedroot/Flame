package com.flame.api.gui;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public final class GuiPagedMenu extends GuiMenu {

    private int x1 = 0, y1 = 0, x2 = 8, y2 = 3;
    private Function<GuiSession, List<ItemStack>> provider = s -> Collections.emptyList();
    private BiFunction<GuiSession, ItemStack, GuiElement> renderer = (s, item) -> GuiElement.of(item);

    private int prevSlot = 45;
    private int nextSlot = 53;
    private int indicatorSlot = 49;

    private ItemStack prevItem = named(Material.ARROW, "§cНазад");
    private ItemStack nextItem = named(Material.ARROW, "§aВперед");
    private ItemStack indicatorItem = named(Material.PAPER, "§fСтраница");

    public GuiPagedMenu(String title, int rows) {
        super(title, rows);
        if (rows >= 5) {
            x1 = 0; y1 = 0; x2 = 8; y2 = rows - 2;
            prevSlot = (rows * 9) - 9;
            indicatorSlot = (rows * 9) - 5;
            nextSlot = (rows * 9) - 1;
        }
        render(this::renderPaged);
    }

    public GuiPagedMenu contentArea(int x1, int y1, int x2, int y2) {
        this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
        return this;
    }

    public GuiPagedMenu itemProvider(Function<GuiSession, List<ItemStack>> provider) {
        this.provider = provider;
        return this;
    }

    public GuiPagedMenu itemRenderer(BiFunction<GuiSession, ItemStack, GuiElement> renderer) {
        this.renderer = renderer;
        return this;
    }

    public GuiPagedMenu controls(int prevSlot, int indicatorSlot, int nextSlot) {
        this.prevSlot = prevSlot;
        this.indicatorSlot = indicatorSlot;
        this.nextSlot = nextSlot;
        return this;
    }

    public GuiPagedMenu prevItem(ItemStack item) { this.prevItem = item; return this; }
    public GuiPagedMenu nextItem(ItemStack item) { this.nextItem = item; return this; }
    public GuiPagedMenu indicatorItem(ItemStack item) { this.indicatorItem = item; return this; }

    private void renderPaged(GuiSession session) {
        List<Integer> slots = contentSlots(session.menu().rows());
        List<ItemStack> items = provider.apply(session);
        if (items == null) items = Collections.emptyList();

        int perPage = slots.size();
        int page = Math.max(0, session.state().getInt("page", 0));
        int maxPage = perPage == 0 ? 0 : Math.max(0, (items.size() - 1) / perPage);
        if (page > maxPage) page = maxPage;
        session.state().set("page", page);

        int from = page * perPage;
        int to = Math.min(items.size(), from + perPage);

        for (int i = 0; i < perPage; i++) {
            int slot = slots.get(i);
            int idx = from + i;
            if (idx < to) {
                ItemStack it = items.get(idx);
                GuiElement el = renderer.apply(session, it);
                session.putElement(slot, el);
            } else {
                session.putElement(slot, null);
            }
        }

        if (page > 0) {
            session.putElement(prevSlot, GuiElement.button(prevItem, c -> {
                c.session().state().incr("page", -1, 0);
                c.refresh();
            }));
        } else {
            session.putElement(prevSlot, GuiElement.of(prevItem).cancelClick(true));
        }

        if (page < maxPage) {
            session.putElement(nextSlot, GuiElement.button(nextItem, c -> {
                c.session().state().incr("page", 1, 0);
                c.refresh();
            }));
        } else {
            session.putElement(nextSlot, GuiElement.of(nextItem).cancelClick(true));
        }

        ItemStack ind = indicatorItem.clone();
        ItemMeta meta = ind.getItemMeta();
        if (meta != null) {
            meta.setDisplayName("§fСтраница: §e" + (page + 1) + "§7/§e" + (maxPage + 1));
            ind.setItemMeta(meta);
        }
        session.putElement(indicatorSlot, GuiElement.of(ind).cancelClick(true));
    }

    private List<Integer> contentSlots(int rows) {
        List<Integer> res = new ArrayList<>();
        int maxY = Math.min(rows - 1, y2);
        for (int y = y1; y <= maxY; y++) {
            for (int x = x1; x <= x2; x++) {
                if (x < 0 || x > 8) continue;
                if (y < 0 || y >= rows) continue;
                res.add(y * 9 + x);
            }
        }
        return res;
    }

    private static ItemStack named(Material mat, String name) {
        ItemStack it = new ItemStack(mat);
        ItemMeta meta = it.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            it.setItemMeta(meta);
        }
        return it;
    }
}
