package com.flame.api.gui;

import java.util.Map;

/**
 * author : s0ckett
 * date : 01.02.26
 */
public final class GuiState {

    private final Map<String, Object> backing;

    GuiState(Map<String, Object> backing) {
        this.backing = backing;
    }

    public GuiState set(String key, Object value) {
        backing.put(key, value);
        return this;
    }

    public boolean has(String key) {
        return backing.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        Object v = backing.get(key);
        if (v == null) return null;
        if (!type.isInstance(v)) return null;
        return (T) v;
    }

    public int getInt(String key, int def) {
        Object v = backing.get(key);
        if (v instanceof Number) return ((Number) v).intValue();
        return def;
    }

    public int incr(String key, int delta, int def) {
        int next = getInt(key, def) + delta;
        backing.put(key, next);
        return next;
    }
}
