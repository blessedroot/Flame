package com.flame.api.discord.manager;

import com.flame.api.discord.sender.DiscordWebhookSender;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DiscordManager {

    /**
     * Обычное сообщение через вебхук
     */
    public static void sendWebhook(String url, String content) {
        DiscordWebhookSender.sendText(url, content);
    }
    /**
     * Embed через вебхук
     */
    public static void sendEmbed(String url, String title, String description, Color color) {
        DiscordWebhookSender.sendEmbed(url, title, description, color);
    }
    /**
     *  Embed с полями
     */
    public static void sendEmbedWithFields(String url, String title, String description, Color color, List<DiscordWebhookSender.Field> fields) {
        DiscordWebhookSender.sendEmbedWithFields(url, title, description, color, fields);
    }
    /**
     * Embed с автором
     */
    public static void sendEmbedWithAuthor(String url, String title, String description, Color color, String authorName, String authorIconUrl) {
        DiscordWebhookSender.sendEmbedWithAuthor(url, title, description, color, authorName, authorIconUrl);
    }
    /**
     * async отправка текстового сообщения
     */
    public static CompletableFuture<Void> sendAsync(String url, String content) {
        return CompletableFuture.runAsync(() -> sendWebhook(url, content));
    }
    /**
     * async отправка ембеда
     */
    public static CompletableFuture<Void> sendEmbedAsync(String url, String title, String description, Color color) {
        return CompletableFuture.runAsync(() -> sendEmbed(url, title, description, color));
    }
}
