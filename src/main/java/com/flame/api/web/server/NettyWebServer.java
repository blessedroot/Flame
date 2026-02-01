package com.flame.api.web.server;

import com.flame.api.FlameAPIPlugin;
import com.flame.api.web.handler.HttpRequestHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : s0ckett
 * date : 01.02.26
 */
public class NettyWebServer {

    private static final Logger LOGGER = Logger.getLogger(NettyWebServer.class.getName());

    private final FlameAPIPlugin plugin;
    private final int port;
    private final String host;
    private final Map<String, RouteHandler> routes = new ConcurrentHashMap<>();

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private Channel serverChannel;
    private boolean running;

    public NettyWebServer(FlameAPIPlugin plugin) {
        this(plugin, 8080, "0.0.0.0");
    }

    public NettyWebServer(FlameAPIPlugin plugin, int port) {
        this(plugin, port, "0.0.0.0");
    }

    public NettyWebServer(FlameAPIPlugin plugin, int port, String host) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port must be between 1 and 65535");
        }

        this.plugin = plugin;
        this.port = port;
        this.host = host;
        this.running = false;
    }

    /**
     * Запустить сервер
     */
    public boolean start() {
        if (running) {
            LOGGER.warning("Server is already running");
            return false;
        }

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup(4); // 4 рабочих потока

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new HttpObjectAggregator(65536));

                            pipeline.addLast(new HttpRequestHandler(routes));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            registerDefaultRoutes();

            ChannelFuture future = bootstrap.bind(host, port).sync();
            serverChannel = future.channel();
            running = true;

            LOGGER.info("Netty web server started on " + host + ":" + port);
            return true;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed to start Netty web server", e);
            shutdown();
            return false;
        }
    }

    public void stop() {
        if (!running) {
            LOGGER.warning("Server is not running");
            return;
        }

        shutdown();
        LOGGER.info("Netty web server stopped");
    }

    private void shutdown() {
        running = false;

        if (serverChannel != null) {
            serverChannel.close();
            serverChannel = null;
        }

        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }

        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }
    }

    private void registerDefaultRoutes() {
        addGetRoute("/api/health", (ctx, request) ->
                sendJson(ctx, "{\"ok\":true,\"service\":\"flame\"}"));

        addGetRoute("/api/server", (ctx, request) -> Bukkit.getScheduler().runTask(plugin, () -> {
            int online = Bukkit.getOnlinePlayers().size();
            int max = Bukkit.getMaxPlayers();
            String version = escapeJson(Bukkit.getVersion());
            String name = escapeJson(Bukkit.getServerName());

            String json = "{"
                    + "\"online\":" + online + ","
                    + "\"maxPlayers\":" + max + ","
                    + "\"version\":\"" + version + "\","
                    + "\"serverName\":\"" + name + "\""
                    + "}";

            sendJson(ctx, json);
        }));

        addGetRoute("/api/player/{name}", (ctx, request) -> Bukkit.getScheduler().runTask(plugin, () -> {
            String uri = request.getUri();
            String path = uri.contains("?") ? uri.substring(0, uri.indexOf("?")) : uri;
            String[] parts = path.split("/");
            String playerName = parts.length >= 4 ? parts[3] : null;

            if (playerName == null || playerName.isEmpty()) {
                sendError(ctx, HttpResponseStatus.BAD_REQUEST, "Player name is required");
                return;
            }

            Player player = Bukkit.getPlayerExact(playerName);
            if (player == null) {
                sendError(ctx, HttpResponseStatus.NOT_FOUND, "Player not found");
                return;
            }

            String json = "{"
                    + "\"name\":\"" + escapeJson(player.getName()) + "\","
                    + "\"uuid\":\"" + player.getUniqueId() + "\","
                    + "\"health\":" + ((int) player.getHealth()) + ","
                    + "\"level\":" + player.getLevel() + ","
                    + "\"world\":\"" + escapeJson(player.getWorld().getName()) + "\""
                    + "}";

            sendJson(ctx, json);
        }));
    }

    public void addRoute(String path, RouteHandler handler) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        if (handler == null) {
            throw new IllegalArgumentException("Handler cannot be null");
        }

        routes.put(path, handler);
        LOGGER.info("Registered route: " + path);
    }

    public void addGetRoute(String path, RouteHandler handler) {
        addRoute(path, (ctx, request) -> {
            if (request.getMethod() == HttpMethod.GET) {
                handler.handle(ctx, request);
            } else {
                sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED, "Method not allowed");
            }
        });
    }

    public void addPostRoute(String path, RouteHandler handler) {
        addRoute(path, (ctx, request) -> {
            if (request.getMethod() == HttpMethod.POST) {
                handler.handle(ctx, request);
            } else {
                sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED, "Method not allowed");
            }
        });
    }

    public void removeRoute(String path) {
        routes.remove(path);
        LOGGER.info("Removed route: " + path);
    }

    public static void sendJson(ChannelHandlerContext ctx, String json) {
        sendJson(ctx, HttpResponseStatus.OK, json);
    }

    public static void sendJson(ChannelHandlerContext ctx, HttpResponseStatus status, String json) {
        byte[] content = json.getBytes(io.netty.util.CharsetUtil.UTF_8);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status,
                io.netty.buffer.Unpooled.wrappedBuffer(content)
        );

        response.headers().set("Content-Type", "application/json; charset=UTF-8");
        response.headers().set("Content-Length", content.length);
        response.headers().set("Access-Control-Allow-Origin", "*");
        response.headers().set("Connection", "close");

        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    public static void sendError(ChannelHandlerContext ctx, HttpResponseStatus status, String message) {
        String json = "{\"error\":\"" + escapeJson(message) + "\",\"status\":" + status.code() + "}";
        sendJson(ctx, status, json);
    }

    private static String escapeJson(String str) {
        if (str == null) return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public Map<String, RouteHandler> getRoutes() {
        return new ConcurrentHashMap<>(routes);
    }

    public boolean isRunning() {
        return running;
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getAddress() {
        return host + ":" + port;
    }

    @FunctionalInterface
    public interface RouteHandler {
        void handle(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception;
    }
}