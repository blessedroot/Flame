package com.flame.api.hologram.impl;

import com.flame.api.hologram.Hologram;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class HologramImpl implements Hologram {

    /**
     * author : s0ckett
     * date : 08.01.25
     */

    private final UUID id;
    private final ArmorStand armorStand;

    public HologramImpl(Location location, String text) {
        this.id = UUID.randomUUID();
        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(text);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public Location getLocation() {
        return armorStand.getLocation();
    }

    @Override
    public void setText(String text) {
        armorStand.setCustomName(text);
    }

    @Override
    public String getText() {
        return armorStand.getCustomName();
    }

    @Override
    public void teleport(Location location) {
        armorStand.teleport(location);
    }

    @Override
    public void delete() {
        armorStand.remove();
    }
}