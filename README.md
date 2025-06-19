<div align="center">

---
# Flame

Minecraft API 1.8.9 <p>

---
</div>

### Обратная связь

+ **[ТГК](https://t.me/playboyjava)**

---

## ну и говнецо?

**Flame** - это API, которая гораздо упростит работу <p>
 Реализовано: **Scoreboard, Hologram, Items, Gui, Npc, WebServer**

---

## Использование

# пук авен

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

# Gradle

```gradle
repositories {
	mavenCentral()
maven { url 'https://jitpack.io' }
		}
	}
```

```gradle
	dependencies {
	        implementation 'com.github.blessedroot:Flame:ВЕРСИЯ'
	}
```

---

## Примеры использования

### 📊 Scoreboard

```java
FlameScoreboard scoreboard = new FlameScoreboard("§cFlame");

scoreboard.setLine(1, "§7Убийств: §c0"); // простая строка
scoreboard.setLine(2, "§7Смертей: §c0");
scoreboard.setEmptyLine(3); // пустая строка

scoreboard.show(player); // показать игроку борд
```

---

### 🧍 NPC

```java
FlameNpc npc = new NpcBuilder()
    .name("§eНавигатор")
    .skin("???")
    .location(player.getLocation())
    .lookAtPlayer(true или false) // Нпс смотрит на игрока как на hypixel
    .build();

npc.spawn(player); // показать NPC игроку

NpcClickManager.onClick(npc, (p, click) -> {
    p.sendMessage("§aклик-клак!");
});
```

---
