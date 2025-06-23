package com.flame.api.npc.fetcher;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;

import java.util.UUID;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class Skin {

    public static WrappedGameProfile fetchSkin(String playerName) {
        GameProfile profile = new GameProfile(UUID.randomUUID(), playerName);
        profile.getProperties().put("textures", new com.mojang.authlib.properties.Property(
                "textures",
                "<base64>",
                "<signature>"
        ));
        return WrappedGameProfile.fromHandle(profile);
    }
}
