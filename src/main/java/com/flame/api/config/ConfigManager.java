package com.flame.api.config;

import com.flame.api.FlameAPIPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * author : s0ckett
 * date : 01.02.26
 */

public class ConfigManager {

    private final FlameAPIPlugin plugin;
    private final Map<String, FlameConfig> configs = new HashMap<>();

    public ConfigManager(FlameAPIPlugin plugin) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        this.plugin = plugin;
    }

    public FlameConfig load(String name) {
        if (configs.containsKey(name)) {
            return configs.get(name);
        }

        FlameConfig config = FlameConfig.create(plugin, name);
        configs.put(name, config);
        return config;
    }

    public FlameConfig get(String name) {
        return configs.get(name);
    }

    public void reload(String name) {
        FlameConfig config = configs.get(name);
        if (config != null) {
            config.reload();
        }
    }

    public void reloadAll() {
        configs.values().forEach(FlameConfig::reload);
    }

    public void save(String name) {
        FlameConfig config = configs.get(name);
        if (config != null) {
            config.save();
        }
    }

    public void saveAll() {
        configs.values().forEach(FlameConfig::save);
    }

    public void unload(String name) {
        FlameConfig config = configs.remove(name);
        if (config != null) {
            config.save();
        }
    }

    public void unloadAll() {
        saveAll();
        configs.clear();
    }

    public boolean isLoaded(String name) {
        return configs.containsKey(name);
    }

    public Set<String> getLoadedConfigs() {
        return configs.keySet();
    }

    public int getLoadedCount() {
        return configs.size();
    }
}