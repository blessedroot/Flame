package com.flame.api.web.handler;

import com.flame.api.web.server.NettyWebServer;
import com.flame.api.web.server.NettyWebServer.RouteHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : s0ckett
 * date : 01.02.26
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private static final Logger LOGGER = Logger.getLogger(HttpRequestHandler.class.getName());

    private final Map<String, RouteHandler> routes;

    public HttpRequestHandler(Map<String, RouteHandler> routes) {
        this.routes = routes;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        try {
            String uri = request.getUri();
            String path = uri.contains("?") ? uri.substring(0, uri.indexOf("?")) : uri;

            RouteHandler handler = routes.get(path);

            if (handler == null) {
                handler = findDynamicRoute(path);
            }

            if (handler != null) {
                handler.handle(ctx, request);
            } else {
                NettyWebServer.sendError(ctx, HttpResponseStatus.NOT_FOUND, "Route not found: " + path);
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error handling request", e);
            NettyWebServer.sendError(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    /**
     * Найти динамик роут
     * пример /api/player/{name}
     */
    private RouteHandler findDynamicRoute(String path) {
        for (Map.Entry<String, RouteHandler> entry : routes.entrySet()) {
            String routePath = entry.getKey();

            if (routePath.contains("{") && routePath.contains("}")) {
                if (matchesPattern(path, routePath)) {
                    return entry.getValue();
                }
            }
        }
        return null;
    }

    private boolean matchesPattern(String path, String pattern) {
        String[] pathParts = path.split("/");
        String[] patternParts = pattern.split("/");

        if (pathParts.length != patternParts.length) {
            return false;
        }

        for (int i = 0; i < pathParts.length; i++) {
            String patternPart = patternParts[i];
            if (patternPart.startsWith("{") && patternPart.endsWith("}")) {
                continue;
            }

            if (!pathParts[i].equals(patternPart)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.log(Level.SEVERE, "Exception in channel", cause);
        ctx.close();
    }
}
