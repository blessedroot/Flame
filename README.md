<div align="center">

# 🔥 Flame
### Minecraft API • 1.8.9

не болей друг! бери флейм и делай

[![JitPack](https://jitpack.io/v/blessedroot/Flame.svg)](https://jitpack.io/#blessedroot/Flame)

**Scoreboard • Hologram • GUI • NPC • Web • Discord • Menu • ActionBar • EventBus • Cooldown • Config • ActionChain**

</div>

---

## 💬 Обратная связь
- **Telegram:** https://t.me/

---

## ❓ Что это вообще такое?
**Flame** — лёгкое и удобное API для **Minecraft 1.8.9**, которое закрывает рутину и даёт быстрый старт:
- понятный API
- готовые модули из коробки

---

## 🚀 Возможности

| Модуль | Описание |
|------|---------|
| 📊 Scoreboard | Динамические борды без боли |
| 🧍 NPC | NPC с кликами и поворотом к игроку |
| 🪧 Hologram | Голограммы над головой |
| 🎒 Items | Удобная работа с предметами |
| 🔔 ActionBar | Экшн-бар сообщения |
| 🧠 EventBus | Свой event bus |
| ⏱ Cooldown | Таймеры и кулдауны |
| ⚙️ Config | Конфиги без страданий |
| 🔗 ActionChain | Цепочки действий |
| 🌐 WebServer | Встроенный веб-сервер |
| 🤖 Discord | Интеграция с Discord |

---

## 📦 Установка

### Maven
```xml
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>
```

```xml
<dependencies>
  <dependency>
    <groupId>com.github.blessedroot</groupId>
    <artifactId>Flame</artifactId>
    <version>ВЕРСИЯ</version>
  </dependency>
</dependencies>
```

### Gradle
```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}
```

```gradle
dependencies {
    implementation 'com.github.blessedroot:Flame:ВЕРСИЯ'
}
```

---

## 🧪 Примеры использования

### 📊 Scoreboard
```java
FlameScoreboard scoreboard = new FlameScoreboard("§cFlame");

scoreboard.setLine(1, "§7Убийств: §c0");
scoreboard.setLine(2, "§7Смертей: §c0");
scoreboard.setEmptyLine(3);

scoreboard.show(player);
```

---

### 🧍 NPC
```java
FlameNpc npc = new NpcBuilder()
    .name("§eНавигатор")
    .skin("???")
    .location(player.getLocation())
    .lookAtPlayer(true) // как на Hypixel
    .build();

npc.spawn(player);

NpcClickManager.onClick(npc, (p, click) -> {
    p.sendMessage("§aклик-клак!");
});
```

---

## 🧠
- ⚡ быстро
- 🧼 чисто
- 🧩 модульно

---

## 🧯 Статус проекта
- 🔧 В активной разработке
- 💡 Идеи приветствуются

---

<div align="center">

</div>
