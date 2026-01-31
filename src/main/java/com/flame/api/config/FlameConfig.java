package com.flame.api.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

/**
 * author : s0ckett
 * date : 01.02.26
 */

public class FlameConfig {

    private final JavaPlugin plugin;
    private final File file;
    private final String resourcePath;
    private FileConfiguration config;

    private FlameConfig(JavaPlugin plugin, String name) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Config name cannot be null or empty");
        }

        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), name.endsWith(".yml") ? name : name + ".yml");
        this.resourcePath = "/" + file.getName();
        reload();
    }

    public static FlameConfig create(JavaPlugin plugin, String name) {
        return new FlameConfig(plugin, name);
    }

    public void reload() {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try (InputStream is = plugin.getResource(file.getName())) {
                if (is != null) {
                    Files.copy(is, file.toPath());
                } else {
                    file.createNewFile();
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Could not create config file: " + file.getName(), e);
            }
        }

        config = YamlConfiguration.loadConfiguration(file);

        InputStream defConfigStream = plugin.getResource(file.getName());
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
            config.setDefaults(defConfig);
        }
    }

    public void save() {
        if (config == null || file == null) {
            return;
        }

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + file.getName(), e);
        }
    }

    public boolean saveDefaultConfig() {
        if (file.exists()) {
            return false;
        }

        plugin.saveResource(file.getName(), false);
        reload();
        return true;
    }

    public void reset() {
        if (file.exists()) {
            file.delete();
        }
        reload();
    }

    public FileConfiguration get() {
        if (config == null) {
            reload();
        }
        return config;
    }

    public boolean exists() {
        return file.exists();
    }

    public File getFile() {
        return file;
    }

    public String getString(String path) {
        return config.getString(path);
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public int getInt(String path) {
        return config.getInt(path);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(path);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public double getDouble(String path) {
        return config.getDouble(path);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public long getLong(String path, long def) {
        return config.getLong(path, def);
    }

    public List<?> getList(String path) {
        return config.getList(path);
    }

    public List<?> getList(String path, List<?> def) {
        return config.getList(path, def);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public List<Integer> getIntegerList(String path) {
        return config.getIntegerList(path);
    }

    public ConfigurationSection getSection(String path) {
        return config.getConfigurationSection(path);
    }

    public Set<String> getKeys(boolean deep) {
        return config.getKeys(deep);
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public boolean isSet(String path) {
        return config.isSet(path);
    }

    public void addDefault(String path, Object value) {
        config.addDefault(path, value);
    }

    public void setDefaults(FileConfiguration defaults) {
        config.setDefaults(defaults);
    }

    public ConfigurationSection createSection(String path) {
        return config.createSection(path);
    }

    public Object get(String path) {
        return config.get(path);
    }

    public Object get(String path, Object def) {
        return config.get(path, def);
    }

    public void copyDefaults(boolean value) {
        config.options().copyDefaults(value);
    }

    public FlameConfig autoSave() {
        save();
        return this;
    }

    public FlameConfig autoReload() {
        reload();
        return this;
    }

    @Override
    public String toString() {
        return "FlameConfig{file=" + file.getName() + ", exists=" + exists() + "}";
    }
}