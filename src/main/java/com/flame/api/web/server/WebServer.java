package com.flame.api.web.server;

import com.flame.api.web.handler.PlayerDataHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class WebServer {

    private HttpServer server;

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/api/player", new PlayerDataHandler());
            server.setExecutor(null);
            server.start();
            System.out.println("WebServer started on port 8080.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(1);
        }
    }
}
