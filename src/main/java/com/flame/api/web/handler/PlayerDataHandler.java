package com.flame.api.web.handler;

import com.flame.api.web.model.ApiPlayer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : s0ckett
 * date : 31.01.26
 */
public class PlayerDataHandler implements HttpHandler {

    private static final Logger LOGGER = Logger.getLogger(PlayerDataHandler.class.getName());

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            if (!"GET".equals(method)) {
                sendError(exchange, 405, "Method Not Allowed");
                return;
            }
            String path = exchange.getRequestURI().getPath();
            String playerName = extractPlayerName(path);

            if (playerName == null || playerName.isEmpty()) {
                sendError(exchange, 400, "Player name is required");
                return;
            }

            Player player = Bukkit.getPlayer(playerName);
            if (player == null) {
                sendError(exchange, 404, "Player not found");
                return;
            }

            Map<String, Object> customData = new HashMap<>();
            customData.put("health", (int) player.getHealth());
            customData.put("level", player.getLevel());
            customData.put("world", player.getWorld().getName());
            customData.put("online", true);

            ApiPlayer apiPlayer = ApiPlayer.fromPlayer(player, 0, customData);

            sendJsonResponse(exchange, 200, apiPlayer.toJson());

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error handling player data request", e);
            sendError(exchange, 500, "Internal Server Error");
        }
    }

    private String extractPlayerName(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }

        String[] parts = path.split("/");
        if (parts.length < 4) {
            return null;
        }

        return parts[3]; // /api/player/{name}
    }


    private void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.sendResponseHeaders(statusCode, response.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response);
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        String json = "{\"error\":\"" + escapeJson(message) + "\",\"status\":" + statusCode + "}";
        sendJsonResponse(exchange, statusCode, json);
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}