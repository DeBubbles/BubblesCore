# 🫧 Bubbles Core

Bubbles Core is een lichte Java-library voor Spigot/Paper plugins, met handige utilities algemene plugin development.  
Deze library werkt **zonder dat de server een aparte plugin hoeft te draaien**—je shadet hem gewoon in je eigen plugin.

---

## 🚀 Installatie (Maven + JitPack)

Voeg eerst JitPack toe aan je repositories:

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
        <groupId>com.github.DeBubbles</groupId>
        <artifactId>BubblesCore</artifactId> <!-- repo naam -->
        <version>1.0.0</version> <!-- vervang door een release/tag -->
    </dependency>
</dependencies>
```
---
## 🧱 Items

Bubbles Core bevat een `ItemBuilder` om eenvoudig custom items te maken met kleuren, lore, enchants, flags en glow-effecten.

### ▶ Voorbeeld (Material)

```java
ItemStack item = ItemBuilder.of(Material.DIAMOND_SWORD)
        .name("&bBubbles &fSword")
        .lore("&7Een custom item.", "<#55ffff>Met hex kleuring!")
        .enchant(Enchantment.DAMAGE_ALL, 5, true)
        .unbreakable(true)
        .glow(true)
        .build();
```
---
## 🎨 Kleuren (ColorUtil)

Bubbles Core bevat een `ColorUtil` helper voor het converteren van kleurcodes in messages en itemnamen.

Ondersteunt:

- **`&` legacy Minecraft kleurcodes**
- **Hex kleuren zoals `<#55ffff>`** (1.16+)
- **Prefix injectie**
- Werkt voor zowel `String` als `List<String>`

### ▶ Voorbeeld

```java
String msg = ColorUtil.color("&bBubbles &7is cool!");
player.sendMessage(msg);
```
---
## 🧱 XMaterial (Versie-onafhankelijke materialen)

`XMaterial` maakt het mogelijk om materialen te gebruiken zonder direct afhankelijk te zijn van de serverversie.  
Momenteel gebruikt Bubbles Core de moderne naamgevingen, maar ondersteunt later automatisch legacy mappings.

### ▶ Voorbeeld

```java
ItemStack emerald = ItemBuilder.of(XMaterial.EMERALD)
        .name("&aBubbles Emerald")
        .glow(true)
        .build();
```
---
