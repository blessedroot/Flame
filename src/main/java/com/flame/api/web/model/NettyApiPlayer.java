package com.flame.api.web.model;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * author : s0ckett
 * date : 01.02.26
 */
public class NettyApiPlayer {

    private final UUID uuid;
    private final String name;
    private final int balance;
    private final Map<String, Object> customData;

    public NettyApiPlayer(UUID uuid, String name, int balance) {
        this(uuid, name, balance, new HashMap<>());
    }

    public NettyApiPlayer(UUID uuid, String name, int balance, Map<String, Object> customData) {
        if (uuid == null) {
            throw new IllegalArgumentException("UUID cannot be null");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }

        this.uuid = uuid;
        this.name = name;
        this.balance = balance;
        this.customData = customData != null ? new HashMap<>(customData) : new HashMap<>();
    }

    public static NettyApiPlayer fromPlayer(Player player, int balance) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return new NettyApiPlayer(player.getUniqueId(), player.getName(), balance);
    }

    public static NettyApiPlayer fromPlayer(Player player, int balance, Map<String, Object> customData) {
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null");
        }
        return new NettyApiPlayer(player.getUniqueId(), player.getName(), balance, customData);
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

    public Map<String, Object> getCustomData() {
        return new HashMap<>(customData);
    }

    public Object getCustomData(String key) {
        return customData.get(key);
    }

    public String toJson() {
        StringBuilder json = new StringBuilder("{");
        json.append("\"uuid\":\"").append(uuid.toString()).append("\",");
        json.append("\"name\":\"").append(escapeJson(name)).append("\",");
        json.append("\"balance\":").append(balance);

        if (!customData.isEmpty()) {
            json.append(",\"customData\":{");
            boolean first = true;
            for (Map.Entry<String, Object> entry : customData.entrySet()) {
                if (!first) json.append(",");
                json.append("\"").append(escapeJson(entry.getKey())).append("\":");
                json.append(toJsonValue(entry.getValue()));
                first = false;
            }
            json.append("}");
        }

        json.append("}");
        return json.toString();
    }

    private String toJsonValue(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        } else if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        } else {
            return "\"" + escapeJson(value.toString()) + "\"";
        }
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static class Builder {
        private UUID uuid;
        private String name;
        private int balance;
        private final Map<String, Object> customData = new HashMap<>();

        public Builder uuid(UUID uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder balance(int balance) {
            this.balance = balance;
            return this;
        }

        public Builder addCustomData(String key, Object value) {
            this.customData.put(key, value);
            return this;
        }

        public Builder customData(Map<String, Object> customData) {
            if (customData != null) {
                this.customData.putAll(customData);
            }
            return this;
        }

        public Builder fromPlayer(Player player) {
            if (player != null) {
                this.uuid = player.getUniqueId();
                this.name = player.getName();
            }
            return this;
        }

        public NettyApiPlayer build() {
            return new NettyApiPlayer(uuid, name, balance, customData);
        }
    }

    @Override
    public String toString() {
        return "NettyApiPlayer{uuid=" + uuid + ", name='" + name + "', balance=" + balance + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof NettyApiPlayer)) return false;
        NettyApiPlayer other = (NettyApiPlayer) obj;
        return uuid.equals(other.uuid);
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }
}