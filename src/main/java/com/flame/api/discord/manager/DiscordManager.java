package com.flame.api.discord.manager;

import com.flame.api.discord.sender.DiscordWebhookSender;
import com.flame.api.discord.sender.DiscordWebhookSender.EmbedBuilder;
import com.flame.api.discord.sender.DiscordWebhookSender.Field;
import org.bukkit.Bukkit;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : s0ckett
 * date : 30.01.26
 * комменты потом, впадлу делать
 */
public class DiscordManager {

    private static final Logger LOGGER = Logger.getLogger(DiscordManager.class.getName());
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(
            3,
            r -> {
                Thread thread = new Thread(r, "Discord-Webhook-Thread");
                thread.setDaemon(true);
                return thread;
            }
    );

    private static final Map<String, Long> rateLimitMap = new ConcurrentHashMap<>();
    private static final long RATE_LIMIT_MILLIS = 1000; // 1 секунда между сообщениями

    public static void sendWebhook(String url, String content) {
        if (isRateLimited(url)) {
            LOGGER.warning("Rate limited for webhook: " + url);
            return;
        }
        DiscordWebhookSender.sendText(url, content);
        updateRateLimit(url);
    }

    public static void sendEmbed(String url, String title, String description, Color color) {
        if (isRateLimited(url)) {
            LOGGER.warning("Rate limited for webhook: " + url);
            return;
        }
        DiscordWebhookSender.sendEmbed(url, title, description, color);
        updateRateLimit(url);
    }

    public static void sendEmbedWithFields(String url, String title, String description,
                                           Color color, List<Field> fields) {
        if (isRateLimited(url)) {
            LOGGER.warning("Rate limited for webhook: " + url);
            return;
        }
        DiscordWebhookSender.sendEmbedWithFields(url, title, description, color, fields);
        updateRateLimit(url);
    }

    public static void sendEmbedWithAuthor(String url, String title, String description,
                                           Color color, String authorName, String authorIconUrl) {
        if (isRateLimited(url)) {
            LOGGER.warning("Rate limited for webhook: " + url);
            return;
        }
        DiscordWebhookSender.sendEmbedWithAuthor(url, title, description, color, authorName, authorIconUrl);
        updateRateLimit(url);
    }

    public static void sendCustomEmbed(String url, EmbedBuilder embedBuilder) {
        if (isRateLimited(url)) {
            LOGGER.warning("Rate limited for webhook: " + url);
            return;
        }
        DiscordWebhookSender.sendCustomEmbed(url, embedBuilder);
        updateRateLimit(url);
    }

    public static void sendMultipleEmbeds(String url, List<EmbedBuilder> embeds) {
        if (isRateLimited(url)) {
            LOGGER.warning("Rate limited for webhook: " + url);
            return;
        }
        DiscordWebhookSender.sendMultipleEmbeds(url, embeds);
        updateRateLimit(url);
    }

    public static CompletableFuture<Void> sendAsync(String url, String content) {
        return CompletableFuture.runAsync(() -> sendWebhook(url, content), EXECUTOR)
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "Failed to send async webhook", ex);
                    return null;
                });
    }

    public static CompletableFuture<Void> sendEmbedAsync(String url, String title,
                                                         String description, Color color) {
        return CompletableFuture.runAsync(() -> sendEmbed(url, title, description, color), EXECUTOR)
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "Failed to send async embed", ex);
                    return null;
                });
    }

    public static CompletableFuture<Void> sendEmbedWithFieldsAsync(String url, String title,
                                                                   String description, Color color,
                                                                   List<Field> fields) {
        return CompletableFuture.runAsync(() ->
                        sendEmbedWithFields(url, title, description, color, fields), EXECUTOR)
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "Failed to send async embed with fields", ex);
                    return null;
                });
    }

    public static CompletableFuture<Void> sendCustomEmbedAsync(String url, EmbedBuilder embedBuilder) {
        return CompletableFuture.runAsync(() -> sendCustomEmbed(url, embedBuilder), EXECUTOR)
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "Failed to send async custom embed", ex);
                    return null;
                });
    }

    public static void sendAsyncWithCallback(org.bukkit.plugin.Plugin plugin, String url, String content,
                                             Runnable onSuccess, Runnable onError) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }

        sendAsync(url, content)
                .thenRun(() -> {
                    if (onSuccess != null) {
                        Bukkit.getScheduler().runTask(plugin, onSuccess);
                    }
                })
                .exceptionally(ex -> {
                    if (onError != null) {
                        Bukkit.getScheduler().runTask(plugin, onError);
                    }
                    return null;
                });
    }
    /**
     * тупа удобная тема
     */
    public static void sendSuccess(String url, String title, String description) {
        sendEmbed(url, title, description, Color.GREEN);
    }

    public static void sendError(String url, String title, String description) {
        sendEmbed(url, title, description, Color.RED);
    }

    public static void sendWarning(String url, String title, String description) {
        sendEmbed(url, title, description, Color.ORANGE);
    }

    public static void sendInfo(String url, String title, String description) {
        sendEmbed(url, title, description, Color.CYAN);
    }

    public static CompletableFuture<Void> sendSuccessAsync(String url, String title, String description) {
        return sendEmbedAsync(url, title, description, Color.GREEN);
    }

    public static CompletableFuture<Void> sendErrorAsync(String url, String title, String description) {
        return sendEmbedAsync(url, title, description, Color.RED);
    }

    public static CompletableFuture<Void> sendWarningAsync(String url, String title, String description) {
        return sendEmbedAsync(url, title, description, Color.ORANGE);
    }

    public static CompletableFuture<Void> sendInfoAsync(String url, String title, String description) {
        return sendEmbedAsync(url, title, description, Color.CYAN);
    }

    private static boolean isRateLimited(String url) {
        Long lastSent = rateLimitMap.get(url);
        if (lastSent == null) {
            return false;
        }
        return System.currentTimeMillis() - lastSent < RATE_LIMIT_MILLIS;
    }

    private static void updateRateLimit(String url) {
        rateLimitMap.put(url, System.currentTimeMillis());
    }

    public static void cleanupRateLimitMap() {
        long now = System.currentTimeMillis();
        rateLimitMap.entrySet().removeIf(entry ->
                now - entry.getValue() > 60000 // Удаляем записи старше 1 минуты
        );
    }

    public static void shutdown() {
        EXECUTOR.shutdown();
        try {
            if (!EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }
        rateLimitMap.clear();
        LOGGER.info("Discord webhook executor shutdown complete");
    }

    public static boolean isActive() {
        return !EXECUTOR.isShutdown() && !EXECUTOR.isTerminated();
    }

    public static int getActiveTaskCount() {
        if (EXECUTOR instanceof ThreadPoolExecutor) {
            return ((ThreadPoolExecutor) EXECUTOR).getActiveCount();
        }
        return -1;
    }
}