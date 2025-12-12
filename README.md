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
        <version>v1.0.4</version>
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
---
## ⚡ Commands & SubCommand Systeem

BubblesCore bevat een lichtgewicht command handler die subcommands ondersteunt, inclusief permissies, argument-controle en tabcompletion.

Het werkt zonder framework en blijft volledig aanpasbaar.

---

### ▶ Een command handler registreren

In jouw plugin:

```java
@Override
public void onEnable() {
    BubblesCommandHandler handler = new BubblesCommandHandler("bubbles")
            .register(new ReloadSubCommand())
            .register(new CoinsAddSubCommand());

    Objects.requireNonNull(getCommand("bubbles")).setExecutor(handler);
    Objects.requireNonNull(getCommand("bubbles")).setTabCompleter(handler);
}
```
### ▶ SubCommand Voorbeeld
```java
public class CoinsAddSubCommand extends SubCommand {

    public AddSubCommand() {
        super("add", "bubbles.coins.add", true);
        setArgumentsHint("<speler> <amount>");
        // Verwacht 2 argumenten
    }

    @Override
    public void onCommand(CommandSender sender, List<String> args) {
        String target = args.get(0);
        int amount = Integer.parseInt(args.get(1));
        sender.sendMessage("Je gaf " + amount + " coins aan " + target);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, List<String> args) {
        if (args.size() == 1) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .toList();
        }
        if (args.size() == 2) {
            return List.of("1", "10", "50", "100");
        }
        return List.of();
    }
}
```
--- 
## 🗂️ Player Data (YamlPlayerDataStore)
```java
public class MyPlugin extends JavaPlugin {

    private YamlDataStore<UUID> playerStore;

    @Override
    public void onEnable() {
        playerStore = new YamlDataStore<>(
                this,
                "playerdata",
                uuid -> uuid.toString() // bestandsnaam
        );
    }

    public YamlDataStore<UUID> getPlayerStore() {
        return playerStore;
    }
}
```

### ▶ Voorbeeld 
```java
public class PlayerDataService {

    private final YamlDataStore<UUID> store;

    public PlayerDataService(JavaPlugin plugin) {
        this.store = new YamlDataStore<>(plugin, "playerdata", UUID::toString);
    }
    
    public int getSpeedups(UUID uuid) {
        return store.getInt(uuid, "speedups", 0);
    }

    public int addSpeedups(UUID uuid, int delta) {
        int updated = Math.max(0, getSpeedups(uuid) + delta);
        store.set(uuid, "speedups", updated);
        store.save(uuid);
        return updated;
    }

    public void unload(UUID uuid) {
        store.unload(uuid, true);
    }
}
```

