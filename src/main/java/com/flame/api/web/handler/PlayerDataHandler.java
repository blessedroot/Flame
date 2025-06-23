package com.flame.api.web.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.flame.api.web.model.ApiPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.OutputStream;

/**
 * author : s0ckett
 * date : 23.06.25
 */


public class PlayerDataHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String name = path.substring(path.lastIndexOf('/') + 1);

        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            exchange.sendResponseHeaders(404, -1);
            return;
        }

        ApiPlayer apiPlayer = new ApiPlayer(player.getUniqueId(), player.getName(), 0);
        String json = "{\"uuid\":\"" + apiPlayer.getUuid() + "\",\"name\":\"" + apiPlayer.getName() + "\",\"balance\":" + apiPlayer.getBalance() + "}";

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, json.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(json.getBytes());
        os.close();
    }
}
