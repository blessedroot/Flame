package com.flame.api.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
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
            try (InputStream is = getClass().getResourceAsStream("/" + file.getName())) {
                if (is != null) {
                    try (OutputStream os = new FileOutputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int length;
                        while ((length = is.read(buffer)) > 0) {
                            os.write(buffer, 0, length);
                        }
                    }
                } else {
                    file.createNewFile();
                }
            } catch (IOException ignored) {}
        }
        cfg = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            cfg.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration get() {
        return cfg;
    }

    public boolean exists() {
        return file.exists();
    }

    public File getFile() {
        return file;
    }
}