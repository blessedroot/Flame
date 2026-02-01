package com.flame.api.item.util;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;

/**
 * author : s0ckett
 * date : 02.02.26
 */
public final class NbtItemTag {

    private NbtItemTag() {}

    private static final String TAG_KEY = "flame:item-id";

    public static ItemStack withFlameId(ItemStack input, String id) {
        if (input == null || id == null) return input;

        try {
            String v = Bukkit.getServer().getClass().getPackage().getName();
            String ver = v.substring(v.lastIndexOf('.') + 1);

            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + ver + ".inventory.CraftItemStack");
            Class<?> nmsItemStack = Class.forName("net.minecraft.server." + ver + ".ItemStack");
            Class<?> nbtCompound = Class.forName("net.minecraft.server." + ver + ".NBTTagCompound");

            Method asNmsCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            Method asBukkitCopy = craftItemStack.getMethod("asBukkitCopy", nmsItemStack);

            Object nms = asNmsCopy.invoke(null, input);

            Method getTag = nmsItemStack.getMethod("getTag");
            Method setTag = nmsItemStack.getMethod("setTag", nbtCompound);

            Object tag = getTag.invoke(nms);
            if (tag == null) {
                tag = nbtCompound.getConstructor().newInstance();
            }

            Method setString = nbtCompound.getMethod("setString", String.class, String.class);
            setString.invoke(tag, TAG_KEY, id);
            setTag.invoke(nms, tag);

            return (ItemStack) asBukkitCopy.invoke(null, nms);
        } catch (Throwable ignored) {
            return input;
        }
    }

    public static String readFlameId(ItemStack input) {
        if (input == null) return null;

        try {
            String v = Bukkit.getServer().getClass().getPackage().getName();
            String ver = v.substring(v.lastIndexOf('.') + 1);

            Class<?> craftItemStack = Class.forName("org.bukkit.craftbukkit." + ver + ".inventory.CraftItemStack");
            Class<?> nmsItemStack = Class.forName("net.minecraft.server." + ver + ".ItemStack");
            Class<?> nbtCompound = Class.forName("net.minecraft.server." + ver + ".NBTTagCompound");

            Method asNmsCopy = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
            Object nms = asNmsCopy.invoke(null, input);

            Method getTag = nmsItemStack.getMethod("getTag");
            Object tag = getTag.invoke(nms);
            if (tag == null) return null;

            Method hasKey = nbtCompound.getMethod("hasKey", String.class);
            boolean ok = (boolean) hasKey.invoke(tag, TAG_KEY);
            if (!ok) return null;

            Method getString = nbtCompound.getMethod("getString", String.class);
            String id = (String) getString.invoke(tag, TAG_KEY);
            return (id == null || id.isEmpty()) ? null : id;
        } catch (Throwable ignored) {
            return null;
        }
    }
}
