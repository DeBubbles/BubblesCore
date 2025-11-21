# 🫧 BubblesCore

BubblesCore is een lichte Java-library voor Spigot/Paper plugins, met handige utilities algemene plugin development.  
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
        <artifactId>BubblesCore</artifactId>
        <version>v1.0.0</version>
    </dependency>
</dependencies>
```
---
## 🧱 Items

BubblesCore bevat een `ItemBuilder` om eenvoudig custom items te maken met kleuren, lore, enchants, flags en glow-effecten.

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

BubblesCore bevat een `ColorUtil` helper voor het converteren van kleurcodes in messages en itemnamen.

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
## 🔖 PersistentData Tags (Custom Item Metadata)

BubblesCore ondersteunt het opslaan van custom data in items via Bukkit’s `PersistentDataContainer`.  
Hiermee kun je o.a. item-types, identifiers, waarden en extra informatie veilig opslaan in NBT.

Deze data blijft bestaan tussen restarts en werkt zonder NMS.

---

### ▶ Initialisatie (vereist)

Voeg dit toe in je plugin's `onEnable()`:

```java
@Override
public void onEnable() {
    TagUtil.init(this); // vereist voor namespaced keys
}
```
---
## 🧩 GUI System (Lightweight, zelf event handling)

BubblesCore biedt een lichte basis voor GUIs via `BubblesMenu`.  
Elke GUI is een class die `BubblesMenu` uitbreidt en fungeert als `InventoryHolder`.

---

### ▶ Een GUI aanmaken

Maak een class die `BubblesMenu` extend:

```java
public class ExampleMenu extends GUI {

    public ExampleMenu(Player viewer) {
        super(viewer, 27, "&bVoorbeeld Menu");
    }

    @Override
    protected void setupItems() {
        inventory.setItem(13, ItemBuilder.of(Material.EMERALD)
                .name("&aKlik mij!")
                .build());
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        // jouw eigen logica
        if (event.getRawSlot() == 13) {
            viewer.sendMessage("Je klikte op de emerald!");
            event.setCancelled(true); // zelf kiezen ✨
        }
    }
}
```
