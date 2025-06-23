package com.flame.api.web.model;

import java.util.UUID;

/**
 * author : s0ckett
 * date : 23.06.25
 */


public class ApiPlayer {
    private final UUID uuid;
    private final String name;
    private final int balance;

    public ApiPlayer(UUID uuid, String name, int balance) {
        this.uuid = uuid;
        this.name = name;
        this.balance = balance;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }
}
