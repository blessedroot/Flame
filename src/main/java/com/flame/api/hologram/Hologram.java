package com.flame.api.hologram;

import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

/**
 * author : s0ckett
 * date : 30.01.26
 */
public interface Hologram {

    UUID getId();

    Location getLocation();

    void setText(String text);

    void setLines(List<String> lines);

    String getText();

    List<String> getLines();

    void teleport(Location location);

    void delete();

    boolean isDeleted();

    void setVisibility(boolean visible);

    boolean isVisible();
}