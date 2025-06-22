package com.flame.api.discord.sender;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class DiscordWebhookSender {

    public static void sendText(String webhookUrl, String content) {
        try {
            JSONObject json = new JSONObject();
            json.put("content", content);
            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            Bukkit.getLogger().warning("Webhook error: " + e.getMessage());
        }
    }

    public static void sendEmbed(String webhookUrl, String title, String description, Color color) {
        try {
            JSONObject json = new JSONObject();
            JSONArray embeds = new JSONArray();

            JSONObject embed = new JSONObject();
            embed.put("title", title);
            embed.put("description", description);
            embed.put("color", color.getRGB() & 0xFFFFFF);

            embeds.add(embed);
            json.put("embeds", embeds);

            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            Bukkit.getLogger().warning("Embed error: " + e.getMessage());
        }
    }

    public static void sendEmbedWithFields(String webhookUrl, String title, String description, Color color, List<Field> fields) {
        try {
            JSONObject json = new JSONObject();
            JSONArray embeds = new JSONArray();
            JSONObject embed = new JSONObject();

            embed.put("title", title);
            embed.put("description", description);
            embed.put("color", color.getRGB() & 0xFFFFFF);

            JSONArray fieldArray = new JSONArray();
            for (Field field : fields) {
                JSONObject obj = new JSONObject();
                obj.put("name", field.name);
                obj.put("value", field.value);
                obj.put("inline", field.inline);
                fieldArray.add(obj);
            }
            embed.put("fields", fieldArray);

            embeds.add(embed);
            json.put("embeds", embeds);

            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            Bukkit.getLogger().warning("Embed with Fields error: " + e.getMessage());
        }
    }

    public static void sendEmbedWithAuthor(String webhookUrl, String title, String description, Color color, String authorName, String authorIconUrl) {
        try {
            JSONObject json = new JSONObject();
            JSONArray embeds = new JSONArray();
            JSONObject embed = new JSONObject();

            embed.put("title", title);
            embed.put("description", description);
            embed.put("color", color.getRGB() & 0xFFFFFF);

            JSONObject author = new JSONObject();
            author.put("name", authorName);
            author.put("icon_url", authorIconUrl);
            embed.put("author", author);

            embeds.add(embed);
            json.put("embeds", embeds);

            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            Bukkit.getLogger().warning("Embed with Author error: " + e.getMessage());
        }
    }

    private static void post(String webhookUrl, String jsonPayload) throws Exception {
        URL url = new URL(webhookUrl);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("User-Agent", "FlameAPI-DiscordWebhook");
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");

        OutputStream stream = connection.getOutputStream();
        stream.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
        stream.flush();
        stream.close();

        connection.getInputStream().close();
        connection.disconnect();
    }

    public static class Field {
        public final String name;
        public final String value;
        public final boolean inline;

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }
}