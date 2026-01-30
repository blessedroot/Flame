package com.flame.api.discord.sender;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * author : s0ckett
 * date : 30.01.26
 */
public class DiscordWebhookSender {

    private static final Logger LOGGER = Logger.getLogger(DiscordWebhookSender.class.getName());
    private static final int MAX_CONTENT_LENGTH = 2000;
    private static final int MAX_EMBED_TITLE_LENGTH = 256;
    private static final int MAX_EMBED_DESCRIPTION_LENGTH = 4096;
    private static final int MAX_EMBED_FIELDS = 25;
    private static final int MAX_EMBED_FIELD_NAME_LENGTH = 256;
    private static final int MAX_EMBED_FIELD_VALUE_LENGTH = 1024;

    /**
     * Отправляет простое текстовое сообщение
     */
    public static void sendText(String webhookUrl, String content) {
        if (!validateWebhookUrl(webhookUrl) || !validateContent(content)) {
            return;
        }

        try {
            JSONObject json = new JSONObject();
            json.put("content", truncate(content, MAX_CONTENT_LENGTH));
            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send text webhook", e);
        }
    }

    /**
     * Отправляет простой эмбд
     */
    public static void sendEmbed(String webhookUrl, String title, String description, Color color) {
        if (!validateWebhookUrl(webhookUrl)) {
            return;
        }

        try {
            JSONObject json = new JSONObject();
            JSONArray embeds = new JSONArray();

            JSONObject embed = createEmbed(title, description, color);
            embeds.add(embed);
            json.put("embeds", embeds);

            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send embed webhook", e);
        }
    }

    /**
     * Отправляет эмбд с полями
     */
    public static void sendEmbedWithFields(String webhookUrl, String title, String description,
                                           Color color, List<Field> fields) {
        if (!validateWebhookUrl(webhookUrl) || fields == null) {
            return;
        }

        if (fields.size() > MAX_EMBED_FIELDS) {
            LOGGER.warning("Too many fields (" + fields.size() + "), max is " + MAX_EMBED_FIELDS);
            fields = fields.subList(0, MAX_EMBED_FIELDS);
        }

        try {
            JSONObject json = new JSONObject();
            JSONArray embeds = new JSONArray();
            JSONObject embed = createEmbed(title, description, color);

            JSONArray fieldArray = new JSONArray();
            for (Field field : fields) {
                if (field != null && field.name != null && field.value != null) {
                    JSONObject fieldObj = new JSONObject();
                    fieldObj.put("name", truncate(field.name, MAX_EMBED_FIELD_NAME_LENGTH));
                    fieldObj.put("value", truncate(field.value, MAX_EMBED_FIELD_VALUE_LENGTH));
                    fieldObj.put("inline", field.inline);
                    fieldArray.add(fieldObj);
                }
            }
            embed.put("fields", fieldArray);

            embeds.add(embed);
            json.put("embeds", embeds);

            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send embed with fields", e);
        }
    }

    /**
     * Отправляет эмбд с автором
     */
    public static void sendEmbedWithAuthor(String webhookUrl, String title, String description,
                                           Color color, String authorName, String authorIconUrl) {
        if (!validateWebhookUrl(webhookUrl)) {
            return;
        }

        try {
            JSONObject json = new JSONObject();
            JSONArray embeds = new JSONArray();
            JSONObject embed = createEmbed(title, description, color);

            if (authorName != null && !authorName.isEmpty()) {
                JSONObject author = new JSONObject();
                author.put("name", truncate(authorName, MAX_EMBED_TITLE_LENGTH));
                if (authorIconUrl != null && !authorIconUrl.isEmpty()) {
                    author.put("icon_url", authorIconUrl);
                }
                embed.put("author", author);
            }

            embeds.add(embed);
            json.put("embeds", embeds);

            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send embed with author", e);
        }
    }

    /**
     * Отправляет кастомный эмбд (юзай EmbedBuilder блять!)
     */
    public static void sendCustomEmbed(String webhookUrl, EmbedBuilder embedBuilder) {
        if (!validateWebhookUrl(webhookUrl) || embedBuilder == null) {
            return;
        }

        try {
            JSONObject json = new JSONObject();
            JSONArray embeds = new JSONArray();
            embeds.add(embedBuilder.build());
            json.put("embeds", embeds);

            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send custom embed", e);
        }
    }

    /**
     * Отправляет сообщение с несколькими эмбедами
     */
    public static void sendMultipleEmbeds(String webhookUrl, List<EmbedBuilder> embedBuilders) {
        if (!validateWebhookUrl(webhookUrl) || embedBuilders == null || embedBuilders.isEmpty()) {
            return;
        }

        try {
            JSONObject json = new JSONObject();
            JSONArray embeds = new JSONArray();

            for (EmbedBuilder builder : embedBuilders) {
                if (builder != null) {
                    embeds.add(builder.build());
                }
            }

            json.put("embeds", embeds);
            post(webhookUrl, json.toJSONString());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Failed to send multiple embeds", e);
        }
    }

    /**
     * Создаёт базовый эмбд объект
     */
    private static JSONObject createEmbed(String title, String description, Color color) {
        JSONObject embed = new JSONObject();

        if (title != null && !title.isEmpty()) {
            embed.put("title", truncate(title, MAX_EMBED_TITLE_LENGTH));
        }

        if (description != null && !description.isEmpty()) {
            embed.put("description", truncate(description, MAX_EMBED_DESCRIPTION_LENGTH));
        }

        if (color != null) {
            embed.put("color", color.getRGB() & 0xFFFFFF);
        }

        return embed;
    }

    /**
     * POST запрос к webhook
     */
    private static void post(String webhookUrl, String jsonPayload) throws IOException {
        URL url = new URL(webhookUrl);
        HttpsURLConnection connection = null;

        try {
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("User-Agent", "FlameAPI-DiscordWebhook/2.0");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);

            try (OutputStream stream = connection.getOutputStream()) {
                stream.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
                stream.flush();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                LOGGER.warning("Discord webhook returned error code: " + responseCode);
            }

        } finally {
            if (connection != null) {
                try {
                    connection.getInputStream().close();
                } catch (IOException ignored) {}
                connection.disconnect();
            }
        }
    }

    private static boolean validateWebhookUrl(String url) {
        if (url == null || url.isEmpty()) {
            LOGGER.warning("Webhook URL cannot be null or empty");
            return false;
        }
        if (!url.startsWith("https://discord.com/api/webhooks/") &&
                !url.startsWith("https://discordapp.com/api/webhooks/")) {
            LOGGER.warning("Invalid Discord webhook URL: " + url);
            return false;
        }
        return true;
    }

    private static boolean validateContent(String content) {
        if (content == null || content.isEmpty()) {
            LOGGER.warning("Content cannot be null or empty");
            return false;
        }
        return true;
    }

    private static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    public static class Field {
        public final String name;
        public final String value;
        public final boolean inline;

        public Field(String name, String value) {
            this(name, value, false);
        }

        public Field(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }

        @Override
        public String toString() {
            return "Field{name='" + name + "', value='" + value + "', inline=" + inline + "}";
        }
    }

    /**
     * билдер
     */
    public static class EmbedBuilder {
        private String title;
        private String description;
        private Color color;
        private String url;
        private String authorName;
        private String authorUrl;
        private String authorIconUrl;
        private String thumbnailUrl;
        private String imageUrl;
        private String footerText;
        private String footerIconUrl;
        private Instant timestamp;
        private final List<Field> fields = new ArrayList<>();

        public EmbedBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public EmbedBuilder setDescription(String description) {
            this.description = description;
            return this;
        }

        public EmbedBuilder setColor(Color color) {
            this.color = color;
            return this;
        }

        public EmbedBuilder setUrl(String url) {
            this.url = url;
            return this;
        }

        public EmbedBuilder setAuthor(String name) {
            this.authorName = name;
            return this;
        }

        public EmbedBuilder setAuthor(String name, String url, String iconUrl) {
            this.authorName = name;
            this.authorUrl = url;
            this.authorIconUrl = iconUrl;
            return this;
        }

        public EmbedBuilder setThumbnail(String url) {
            this.thumbnailUrl = url;
            return this;
        }

        public EmbedBuilder setImage(String url) {
            this.imageUrl = url;
            return this;
        }

        public EmbedBuilder setFooter(String text) {
            this.footerText = text;
            return this;
        }

        public EmbedBuilder setFooter(String text, String iconUrl) {
            this.footerText = text;
            this.footerIconUrl = iconUrl;
            return this;
        }

        public EmbedBuilder setTimestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public EmbedBuilder setTimestampToNow() {
            this.timestamp = Instant.now();
            return this;
        }

        public EmbedBuilder addField(String name, String value) {
            return addField(name, value, false);
        }

        public EmbedBuilder addField(String name, String value, boolean inline) {
            if (fields.size() < MAX_EMBED_FIELDS) {
                fields.add(new Field(name, value, inline));
            }
            return this;
        }

        public EmbedBuilder addBlankField(boolean inline) {
            return addField("\u200B", "\u200B", inline);
        }

        public EmbedBuilder clearFields() {
            fields.clear();
            return this;
        }

        public JSONObject build() {
            JSONObject embed = new JSONObject();

            if (title != null && !title.isEmpty()) {
                embed.put("title", truncate(title, MAX_EMBED_TITLE_LENGTH));
            }

            if (description != null && !description.isEmpty()) {
                embed.put("description", truncate(description, MAX_EMBED_DESCRIPTION_LENGTH));
            }

            if (url != null && !url.isEmpty()) {
                embed.put("url", url);
            }

            if (color != null) {
                embed.put("color", color.getRGB() & 0xFFFFFF);
            }

            if (authorName != null && !authorName.isEmpty()) {
                JSONObject author = new JSONObject();
                author.put("name", truncate(authorName, MAX_EMBED_TITLE_LENGTH));
                if (authorUrl != null && !authorUrl.isEmpty()) {
                    author.put("url", authorUrl);
                }
                if (authorIconUrl != null && !authorIconUrl.isEmpty()) {
                    author.put("icon_url", authorIconUrl);
                }
                embed.put("author", author);
            }

            if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
                JSONObject thumbnail = new JSONObject();
                thumbnail.put("url", thumbnailUrl);
                embed.put("thumbnail", thumbnail);
            }

            if (imageUrl != null && !imageUrl.isEmpty()) {
                JSONObject image = new JSONObject();
                image.put("url", imageUrl);
                embed.put("image", image);
            }

            if (footerText != null && !footerText.isEmpty()) {
                JSONObject footer = new JSONObject();
                footer.put("text", truncate(footerText, 2048));
                if (footerIconUrl != null && !footerIconUrl.isEmpty()) {
                    footer.put("icon_url", footerIconUrl);
                }
                embed.put("footer", footer);
            }

            if (timestamp != null) {
                embed.put("timestamp", timestamp.toString());
            }

            if (!fields.isEmpty()) {
                JSONArray fieldArray = new JSONArray();
                for (Field field : fields) {
                    if (field != null && field.name != null && field.value != null) {
                        JSONObject fieldObj = new JSONObject();
                        fieldObj.put("name", truncate(field.name, MAX_EMBED_FIELD_NAME_LENGTH));
                        fieldObj.put("value", truncate(field.value, MAX_EMBED_FIELD_VALUE_LENGTH));
                        fieldObj.put("inline", field.inline);
                        fieldArray.add(fieldObj);
                    }
                }
                embed.put("fields", fieldArray);
            }

            return embed;
        }
    }
}