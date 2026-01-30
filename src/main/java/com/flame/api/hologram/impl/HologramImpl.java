package com.flame.api.hologram.impl;

import com.flame.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * author : s0ckett
 * date : 30.01.26
 */
public class HologramImpl implements Hologram {

    private static final double LINE_HEIGHT = 0.25;

    private final UUID id;
    private final List<ArmorStand> armorStands;
    private Location location;
    private boolean deleted;
    private boolean visible;

    public HologramImpl(Location location, String text) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location and world cannot be null");
        }
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        this.id = UUID.randomUUID();
        this.location = location.clone();
        this.armorStands = new ArrayList<>();
        this.deleted = false;
        this.visible = true;

        createArmorStand(text, 0);
    }

    public HologramImpl(Location location, List<String> lines) {
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location and world cannot be null");
        }
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Lines cannot be null or empty");
        }

        this.id = UUID.randomUUID();
        this.location = location.clone();
        this.armorStands = new ArrayList<>();
        this.deleted = false;
        this.visible = true;

        for (int i = 0; i < lines.size(); i++) {
            createArmorStand(lines.get(i), i);
        }
    }

    private void createArmorStand(String text, int index) {
        Location spawnLocation = location.clone().subtract(0, LINE_HEIGHT * index, 0);
        ArmorStand armorStand = (ArmorStand) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);

        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(text);
        armorStand.setBasePlate(false);
        armorStand.setArms(false);

        armorStands.add(armorStand);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return location.clone();
    }

    @Override
    public void setText(String text) {
        checkNotDeleted();
        if (text == null) {
            throw new IllegalArgumentException("Text cannot be null");
        }

        if (!armorStands.isEmpty()) {
            armorStands.get(0).setCustomName(text);
        }
    }

    @Override
    public void setLines(List<String> lines) {
        checkNotDeleted();
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("Lines cannot be null or empty");
        }

        // Удаляем старые armor stands
        for (ArmorStand stand : armorStands) {
            if (stand != null && stand.isValid()) {
                stand.remove();
            }
        }
        armorStands.clear();

        // Создаём новые
        for (int i = 0; i < lines.size(); i++) {
            createArmorStand(lines.get(i), i);
        }
    }

    @Override
    public String getText() {
        checkNotDeleted();
        if (armorStands.isEmpty()) {
            return "";
        }
        return armorStands.get(0).getCustomName();
    }

    @Override
    public List<String> getLines() {
        checkNotDeleted();
        List<String> lines = new ArrayList<>();
        for (ArmorStand stand : armorStands) {
            if (stand != null && stand.isValid()) {
                lines.add(stand.getCustomName());
            }
        }
        return Collections.unmodifiableList(lines);
    }

    @Override
    public void teleport(Location location) {
        checkNotDeleted();
        if (location == null || location.getWorld() == null) {
            throw new IllegalArgumentException("Location and world cannot be null");
        }

        this.location = location.clone();

        for (int i = 0; i < armorStands.size(); i++) {
            ArmorStand stand = armorStands.get(i);
            if (stand != null && stand.isValid()) {
                Location newLocation = location.clone().subtract(0, LINE_HEIGHT * i, 0);
                stand.teleport(newLocation);
            }
        }
    }

    @Override
    public void delete() {
        if (deleted) {
            return;
        }

        for (ArmorStand stand : armorStands) {
            if (stand != null && stand.isValid()) {
                stand.remove();
            }
        }
        armorStands.clear();
        deleted = true;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setVisibility(boolean visible) {
        checkNotDeleted();
        this.visible = visible;

        for (ArmorStand stand : armorStands) {
            if (stand != null && stand.isValid()) {
                stand.setCustomNameVisible(visible);
            }
        }
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    private void checkNotDeleted() {
        if (deleted) {
            throw new IllegalStateException("Cannot perform operation on deleted hologram");
        }
    }
}