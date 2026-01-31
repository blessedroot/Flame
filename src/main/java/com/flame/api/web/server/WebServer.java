package com.flame.api.web.server;

import com.flame.api.FlameAPIPlugin;
import com.flame.api.web.handler.PlayerDataHandler;
import com.flame.api.web.handler.ServerInfoHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : s0ckett
 * date : 31.01.26
 */
public class WebServer {

    private static final Logger LOGGER = Logger.getLogger(WebServer.class.getName());

    private final FlameAPIPlugin plugin;
    private final int port;
    private final String host;

    private HttpServer server;
    private ThreadPoolExecutor executor;
    private boolean running;

    public WebServer(FlameAPIPlugin plugin) {
        this(plugin, 8080, "0.0.0.0");
    }

    public WebServer(FlameAPIPlugin plugin, int port) {
        this(plugin, port, "0.0.0.0");
    }

    public WebServer(FlameAPIPlugin plugin, int port, String host) {
        if (plugin == null) {
            throw new IllegalArgumentException("FlameAPIPlugin cannot be null");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("Host cannot be null or empty");
        }

        this.plugin = plugin;
        this.port = port;
        this.host = host;
        this.running = false;
    }

    public boolean start() {
        if (running) {
            LOGGER.warning("Web server is already running");
            return false;
        }

        try {
            InetSocketAddress address = new InetSocketAddress(host, port);
            server = HttpServer.create(address, 0);

            executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4, r -> {
                Thread thread = new Thread(r, "Web-Worker");
                thread.setDaemon(true);
                return thread;
            });
            server.setExecutor(executor);

            registerHandlers();

            server.start();
            running = true;

            LOGGER.info("Web server started on " + getAddress());
            return true;

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to start web server", e);
            return false;
        }
    }

    public void stop() {
        if (!running) {
            LOGGER.warning("Web server is not running");
            return;
        }

        try {
            if (server != null) {
                server.stop(2);
                server = null;
            }

            if (executor != null) {
                executor.shutdown();
                executor = null;
            }

            running = false;
            LOGGER.info("Web server stopped");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error stopping web server", e);
        }
    }

    public boolean restart() {
        stop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return start();
    }

    private void registerHandlers() {
        server.createContext("/api/player", new PlayerDataHandler());
        server.createContext("/api/server", new ServerInfoHandler());
    }

    public boolean isRunning() {
        return running;
    }

    public String getAddress() {
        return host + ":" + port;
    }

    public int getActiveThreads() {
        return executor != null ? executor.getActiveCount() : -1;
    }
}
