package com.flame.api.menu.manager;

import com.flame.api.config.FlameConfig;
import com.flame.api.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class MenuManager {

    private final Map<String, Menu> menus = new HashMap<>();
    private final JavaPlugin plugin;

    public MenuManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadMenus();
    }

    public void loadMenus() {
        menus.clear();

        File folder = new File(plugin.getDataFolder(), "menus");
        if (!folder.exists()) {
            folder.mkdirs();
            createExampleMenu(new File(folder, "example.yml"));
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            FlameConfig config = FlameConfig.create(plugin, "menus/" + file.getName());
            FileConfiguration cfg = config.get();
            String title = cfg.getString("menu.title", "§cБез названия");
            int rows = cfg.getInt("menu.rows", 1);
            Menu menu = new Menu(title, rows);

            if (cfg.contains("menu.items")) {
                for (String key : cfg.getConfigurationSection("menu.items").getKeys(false)) {
                    try {
                        int slot = Integer.parseInt(key);
                        String path = "menu.items." + key;

                        String typeName = cfg.getString(path + ".type", "STONE");
                        Material material = Material.matchMaterial(typeName);
                        if (material == null) material = Material.STONE;

                        ItemStack item = new ItemStack(material);
                        ItemMeta meta = item.getItemMeta();

                        if (cfg.contains(path + ".name")) {
                            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', cfg.getString(path + ".name")));
                        }

                        if (cfg.contains(path + ".lore")) {
                            List<String> lore = cfg.getStringList(path + ".lore").stream()
                                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                                    .collect(Collectors.toList());
                            meta.setLore(lore);
                        }

                        item.setItemMeta(meta);

                        String click = cfg.getString(path + ".click");

                        menu.setItem(slot, item, player -> {
                            if (click == null) return;

                            if (click.equalsIgnoreCase("[close]")) {
                                player.closeInventory();

                            } else if (click.startsWith("[command] ")) {
                                String command = click.substring(10);
                                player.performCommand(command);

                            } else if (click.startsWith("[console] ")) {
                                String command = click.substring(10).replace("%player%", player.getName());
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                            }
                        });

                    } catch (Exception e) {
                        Bukkit.getLogger().warning("[FlameAPI] Ошибка загрузки предмета в меню " + file.getName() + " -> слот " + key);
                        e.printStackTrace();
                    }
                }
            }

            menus.put(file.getName().replace(".yml", ""), menu);
        }
    }

    public void openMenu(Player player, String name) {
        Menu menu = menus.get(name);
        if (menu != null) {
            player.openInventory(menu.build());
        }
    }

    public Menu getMenu(String name) {
        return menus.get(name);
    }

    private void createExampleMenu(File file) {
        try {
            file.createNewFile();
            String content = """
                    menu:
                      title: "§6Примерное меню"
                      rows: 3
                      items:
                        "11":
                          type: DIAMOND
                          name: "§bАлмаз"
                          lore:
                            - "§7Это пример"
                            - "§eНажми сюда!"
                          click: "[console] say %player% нажал на алмаз!"

                        "13":
                          type: PAPER
                          name: "§fПомощь"
                          click: "[command] help"

                        "15":
                          type: BARRIER
                          name: "§cЗакрыть"
                          click: "[close]"
                    """;
            Files.write(file.toPath(), content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
