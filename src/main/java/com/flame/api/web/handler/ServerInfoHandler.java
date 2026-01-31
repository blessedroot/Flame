package com.flame.api.web.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * author : s0ckett
 * date : 31.01.26
 */
public class ServerInfoHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            Collection<? extends Player> players = Bukkit.getOnlinePlayers();

            StringBuilder json = new StringBuilder("{");
            json.append("\"online\":").append(players.size()).append(",");
            json.append("\"maxPlayers\":").append(Bukkit.getMaxPlayers()).append(",");
            json.append("\"version\":\"").append(Bukkit.getVersion()).append("\",");
            json.append("\"serverName\":\"").append(Bukkit.getServerName()).append("\",");
            json.append("\"players\":[");

            boolean first = true;
            for (Player player : players) {
                if (!first) json.append(",");
                json.append("\"").append(escapeJson(player.getName())).append("\"");
                first = false;
            }

            json.append("]}");

            sendJsonResponse(exchange, 200, json.toString());

        } catch (Exception e) {
            sendError(exchange, 500, "Internal Server Error");
        }
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
        String json = "{\"error\":\"" + escapeJson(message) + "\"}";
        sendJsonResponse(exchange, statusCode, json);
    }

    private String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}