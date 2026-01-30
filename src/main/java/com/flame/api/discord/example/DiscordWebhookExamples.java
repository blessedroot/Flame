package com.flame.api.discord.example;

import com.flame.api.discord.manager.DiscordManager;
import com.flame.api.discord.sender.DiscordWebhookSender.EmbedBuilder;
import com.flame.api.discord.sender.DiscordWebhookSender.Field;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * author : s0ckett
 * date : 30.01.26
 * комменты потом, впадлу делать
 */
public class DiscordWebhookExamples {

    private static final String WEBHOOK_URL = "https://discord.com/api/webhooks/YOUR_WEBHOOK_URL";

    public static void example1() {
        DiscordManager.sendWebhook(WEBHOOK_URL, "хеллоу мир!");
    }

    public static void example2() {
        DiscordManager.sendEmbed(
                WEBHOOK_URL,
                "Сервер запущен",
                "Minecraft сервер успешно запустился!",
                Color.GREEN
        );
    }

    public static void example3() {
        List<Field> fields = Arrays.asList(
                new Field("Игроков онлайн", "23940130", true),
                new Field("TPS", "тпсик", true),
                new Field("Версия", "версия", true)
        );

        DiscordManager.sendEmbedWithFields(
                WEBHOOK_URL,
                "Статус сервера",
                "Текущая информация о сервере",
                Color.CYAN,
                fields
        );
    }

    public static void example4(Player player) {
        DiscordManager.sendEmbedWithAuthor(
                WEBHOOK_URL,
                "Игрок присоединился",
                "Добро пожаловать на сервер!",
                Color.GREEN,
                player.getName(),
                "https://s0ckett.net/" + player.getUniqueId()
        );
    }

    public static void example5() {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("не")
                .setDescription("бе")
                .setColor(Color.BLUE)
                .setAuthor("s0ckett", null, "https://example.com/icon.png")
                .setThumbnail("https://s0ckett.net/thumbnail.png")
                .setImage("https://s0ckett.net/banner.png")
                .addField("че новово туто??", "хз не шарю", false)
                .addField("date", "30.01.2026", true)
                .addField("ver", "2.0.0", true)
                .setFooter("Flame", "https://s0ckett.net/footer.png")
                .setTimestampToNow();

        DiscordManager.sendCustomEmbed(WEBHOOK_URL, embed);
    }

    public static void example6() {
        EmbedBuilder embed1 = new EmbedBuilder()
                .setTitle("Информация о игроке")
                .setDescription("Статистика игрока")
                .setColor(Color.GREEN)
                .addField("Ник", "Player123", true)
                .addField("Уровень", "50", true);

        EmbedBuilder embed2 = new EmbedBuilder()
                .setTitle("Достижения")
                .setDescription("Последние достижения")
                .setColor(Color.RED)
                .addField("Достижение", "Пососал хуйца у админа", false);

        DiscordManager.sendMultipleEmbeds(WEBHOOK_URL, Arrays.asList(embed1, embed2));
    }

    public static void example7() {
        DiscordManager.sendAsync(WEBHOOK_URL, "Сообщение отправлено асинхронно!")
                .thenRun(() -> System.out.println("Сообщение отправлено!"))
                .exceptionally(ex -> {
                    System.err.println("Ошибка: " + ex.getMessage());
                    return null;
                });
    }

    public static void example8(Player player) {
        DiscordManager.sendAsyncWithCallback(
                WEBHOOK_URL,
                "Игрок " + player.getName() + " зашёл на сервер",
                () -> player.sendMessage("§aУведомление отправлено в Discord!"),
                () -> player.sendMessage("§cОшибка отправки в Discord")
        );
    }

    public static void example9() {
        DiscordManager.sendSuccess(WEBHOOK_URL, "Успех", "ураа, это успешно... как?");
        DiscordManager.sendError(WEBHOOK_URL, "Ошибка", "упс... ошибочка");
        DiscordManager.sendWarning(WEBHOOK_URL, "Предупреждение", "сервак нагружен");
        DiscordManager.sendInfo(WEBHOOK_URL, "Информация", "сервак будет перезапущен через 5 минут");
    }

    public static void example10(String playerName, String reason, String admin) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Игрок забанен")
                .setColor(Color.RED)
                .addField("Игрок", playerName, true)
                .addField("Администратор", admin, true)
                .addField("Причина", reason, false)
                .setFooter("ban system")
                .setTimestampToNow();

        DiscordManager.sendCustomEmbedAsync(WEBHOOK_URL, embed);
    }

    public static void example11(int onlinePlayers, int maxPlayers, double tps) {
        Color color;
        if (tps >= 19.5) {
            color = Color.GREEN;
        } else if (tps >= 18.0) {
            color = Color.ORANGE;
        } else {
            color = Color.RED;
        }

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Статистика")
                .setColor(color)
                .addField("Онлайн", onlinePlayers + "/" + maxPlayers, true)
                .addField("TPS", String.format("%.1f", tps), true)
                .addField("Статус", tps >= 19.5 ? "заебок!" : tps >= 18.0 ? "найс" : "хуета", true)
                .setFooter("Обновляется каждые 5 минут")
                .setTimestampToNow();

        DiscordManager.sendCustomEmbedAsync(WEBHOOK_URL, embed);
    }

    public static void example12(Player player, String itemName, int amount, double price) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Покупка в магазине")
                .setColor(Color.YELLOW)
                .setAuthor(player.getName(), null, "https://s0ckett.net/players/" + player.getUniqueId())
                .addField("Предмет", itemName, true)
                .addField("Количество", String.valueOf(amount), true)
                .addField("Цена", price + " пидорков", true)
                .setFooter("Магазин")
                .setTimestampToNow();

        DiscordManager.sendCustomEmbedAsync(WEBHOOK_URL, embed);
    }

    public static void example13(String errorMessage, String stackTrace) {
        String truncatedStack = stackTrace.length() > 1000
                ? stackTrace.substring(0, 1000) + "..."
                : stackTrace;

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Ошибка")
                .setDescription("**Сообщение:**\n```" + errorMessage + "```")
                .setColor(Color.RED)
                .addField("Stack Trace", "```" + truncatedStack + "```", false)
                .setFooter("Система мониторинга")
                .setTimestampToNow();

        DiscordManager.sendCustomEmbedAsync(WEBHOOK_URL, embed);
    }

    public static void example14(String playerName, String packageName, double amount) {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("донатик")
                .setDescription("погоди ка, кто то мне задонатил!")
                .setColor(new Color(255, 215, 0)) // Золотой
                .addField("Донатер", playerName, true)
                .addField("Пакет", packageName, true)
                .addField("Сумма", amount + " dollar", true)
                .setThumbnail("https://s0ckett.net/donat-icon.png")
                .setFooter("эээх брат зачем так делать...")
                .setTimestampToNow();

        DiscordManager.sendCustomEmbedAsync(WEBHOOK_URL, embed);
    }

    public static void example15() {
        EmbedBuilder embed = new EmbedBuilder()
                .setTitle("Подробная штучка")
                .setDescription("детальный эмбед")
                .setColor(Color.MAGENTA)
                .addField("Поле 1", "Значение 1", true)
                .addField("Поле 2", "Значение 2", true)
                .addField("Поле 3", "Значение 3", true)
                .addBlankField(false)
                .addField("Длинное поле", "Это очень длинное описание, прям как мой хуй", false)
                .addField("Ещё поле", "Значение", true)
                .setFooter("Footer text", "https://s0ckett.net/icon.png")
                .setTimestampToNow();

        DiscordManager.sendCustomEmbed(WEBHOOK_URL, embed);
    }
}