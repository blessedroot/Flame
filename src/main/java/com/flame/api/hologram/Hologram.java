package com.flame.api.hologram;

import org.bukkit.Location;

import java.util.UUID;

public interface Hologram {

    /**
     * author : s0ckett
     * date : 08.01.25
     */

    UUID getId();

    Location getLocation();

    void setText(String text);

    String getText();

    void teleport(Location location);

    void delete();
}