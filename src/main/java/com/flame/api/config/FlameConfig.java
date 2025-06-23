package com.flame.api.config;

import org.bukkit.configuration.file.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;

public class FlameConfig {
    private final File file;
    private FileConfiguration cfg;

    private FlameConfig(JavaPlugin plugin, String name) {
        this.file = new File(plugin.getDataFolder(), name);
        reload();
    }

    public static FlameConfig create(JavaPlugin plugin, String name) {
        return new FlameConfig(plugin, name);
    }

    public void reload() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try (InputStream is = getClass().getResourceAsStream("/" + file.getName());
                 FileOutputStream fos = new FileOutputStream(file)) {
                if (is != null) { byte[] buf = is.readAllBytes(); fos.write(buf); }
            } catch (IOException ignored) {}
        }
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void save() { try { cfg.save(file); } catch (IOException e) { e.printStackTrace(); } }

    public FileConfiguration get() { return cfg; }
}