package com.flame.api.item.impl;

import com.flame.api.item.Item;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * author : s0ckett
 * date : 31.01.26
 */
public class ItemImpl implements Item {

    private final ItemStack itemStack;
    private PlayerAction clickAction;

    /**
     * Конструктор для создания предмета
     * @param material материал
     * @param title название
     * @param description описание
     */
    public ItemImpl(Material material, String title, List<String> description) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }

        this.itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();

        if (meta != null) {
            if (title != null && !title.isEmpty()) {
                meta.setDisplayName(title);
            }
            if (description != null && !description.isEmpty()) {
                meta.setLore(new ArrayList<>(description));
            }
            itemStack.setItemMeta(meta);
        }
    }

    /**
     * Конструктор из существующего ItemStack
     * @param itemStack существующий айтемстакич
     */
    public ItemImpl(ItemStack itemStack) {
        if (itemStack == null) {
            throw new IllegalArgumentException("ItemStack cannot be null");
        }
        this.itemStack = itemStack.clone();
    }

    /**
     * констр. для клонирования
     */
    private ItemImpl(ItemStack itemStack, PlayerAction action) {
        this.itemStack = itemStack.clone();
        this.clickAction = action;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack.clone();
    }

    @Override
    public Material getMaterial() {
        return itemStack.getType();
    }

    @Override
    public String getDisplayName() {
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.hasDisplayName() ? meta.getDisplayName() : "";
    }

    @Override
    public List<String> getLore() {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && meta.hasLore()) {
            return Collections.unmodifiableList(meta.getLore());
        }
        return Collections.emptyList();
    }

    @Override
    public void setClickAction(PlayerAction action) {
        this.clickAction = action;
    }

    @Override
    public PlayerAction getClickAction() {
        return clickAction;
    }

    @Override
    public void executeAction(Player player) {
        if (clickAction != null && player != null) {
            clickAction.execute(player);
        }
    }

    @Override
    public boolean hasAction() {
        return clickAction != null;
    }

    @Override
    public Item clone() {
        return new ItemImpl(this.itemStack, this.clickAction);
    }

    @Override
    public void setAmount(int amount) {
        if (amount > 0 && amount <= 64) {
            itemStack.setAmount(amount);
        }
    }

    @Override
    public int getAmount() {
        return itemStack.getAmount();
    }

    public void setDisplayName(String displayName) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            itemStack.setItemMeta(meta);
        }
    }

    public void setLore(List<String> lore) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            meta.setLore(lore != null ? new ArrayList<>(lore) : null);
            itemStack.setItemMeta(meta);
        }
    }

    public void addLoreLine(String line) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.hasLore() ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add(line);
            meta.setLore(lore);
            itemStack.setItemMeta(meta);
        }
    }

    /**
     * Добавляет зачарование
     * @param enchantment зачарование
     * @param level уровень
     */
    public void addEnchantment(Enchantment enchantment, int level) {
        if (enchantment != null && level > 0) {
            itemStack.addUnsafeEnchantment(enchantment, level);
        }
    }

    /**
     * Удаляет зачарование
     * @param enchantment зачарование
     */
    public void removeEnchantment(Enchantment enchantment) {
        if (enchantment != null) {
            itemStack.removeEnchantment(enchantment);
        }
    }

    /**
     * Добавляет флаг предмета
     * @param flag флаг
     */
    public void addItemFlag(ItemFlag flag) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && flag != null) {
            meta.addItemFlags(flag);
            itemStack.setItemMeta(meta);
        }
    }

    /**
     * Удаляет флаг предмета
     * @param flag флаг
     */
    public void removeItemFlag(ItemFlag flag) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && flag != null) {
            meta.removeItemFlags(flag);
            itemStack.setItemMeta(meta);
        }
    }

    /**
     * Делает предмет неразрушимым
     * @param unbreakable true для неразрушимости
     */
    public void setUnbreakable(boolean unbreakable) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            try {
                meta.getClass().getMethod("setUnbreakable", boolean.class).invoke(meta, unbreakable);
                itemStack.setItemMeta(meta);
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof ItemImpl)) return false;
        ItemImpl other = (ItemImpl) obj;
        return itemStack.isSimilar(other.itemStack);
    }

    @Override
    public int hashCode() {
        return itemStack.hashCode();
    }

    @Override
    public String toString() {
        return "ItemImpl{" +
                "material=" + getMaterial() +
                ", displayName='" + getDisplayName() + '\'' +
                ", amount=" + getAmount() +
                ", hasAction=" + hasAction() +
                '}';
    }

    public static class Builder {
        private Material material;
        private String displayName;
        private List<String> lore = new ArrayList<>();
        private int amount = 1;
        private PlayerAction clickAction;
        private final List<EnchantmentData> enchantments = new ArrayList<>();
        private final List<ItemFlag> itemFlags = new ArrayList<>();
        private boolean unbreakable = false;

        public Builder(Material material) {
            if (material == null) {
                throw new IllegalArgumentException("Material cannot be null");
            }
            this.material = material;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder lore(List<String> lore) {
            if (lore != null) {
                this.lore = new ArrayList<>(lore);
            }
            return this;
        }

        public Builder lore(String... lore) {
            if (lore != null) {
                this.lore = new ArrayList<>();
                Collections.addAll(this.lore, lore);
            }
            return this;
        }

        public Builder addLoreLine(String line) {
            if (line != null) {
                this.lore.add(line);
            }
            return this;
        }

        public Builder amount(int amount) {
            if (amount > 0 && amount <= 64) {
                this.amount = amount;
            }
            return this;
        }

        public Builder clickAction(PlayerAction action) {
            this.clickAction = action;
            return this;
        }

        public Builder enchantment(Enchantment enchantment, int level) {
            if (enchantment != null && level > 0) {
                enchantments.add(new EnchantmentData(enchantment, level));
            }
            return this;
        }

        public Builder glow() {
            return enchantment(Enchantment.DURABILITY, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS);
        }

        public Builder addItemFlag(ItemFlag flag) {
            if (flag != null) {
                itemFlags.add(flag);
            }
            return this;
        }

        public Builder hideAttributes() {
            return addItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        }

        public Builder hideEnchants() {
            return addItemFlag(ItemFlag.HIDE_ENCHANTS);
        }

        public Builder hideUnbreakable() {
            return addItemFlag(ItemFlag.HIDE_UNBREAKABLE);
        }

        public Builder unbreakable(boolean unbreakable) {
            this.unbreakable = unbreakable;
            return this;
        }

        public ItemImpl build() {
            ItemImpl item = new ItemImpl(material, displayName, lore);
            item.setAmount(amount);

            if (clickAction != null) {
                item.setClickAction(clickAction);
            }

            for (EnchantmentData enchData : enchantments) {
                item.addEnchantment(enchData.enchantment, enchData.level);
            }

            for (ItemFlag flag : itemFlags) {
                item.addItemFlag(flag);
            }

            if (unbreakable) {
                item.setUnbreakable(true);
            }

            return item;
        }

        private static class EnchantmentData {
            final Enchantment enchantment;
            final int level;

            EnchantmentData(Enchantment enchantment, int level) {
                this.enchantment = enchantment;
                this.level = level;
            }
        }
    }
}