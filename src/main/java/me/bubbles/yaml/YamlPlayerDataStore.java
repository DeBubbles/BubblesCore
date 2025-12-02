package me.bubbles.yaml;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class YamlPlayerDataStore {

    private final Plugin plugin;
    private final File dir;

    private final Map<UUID, YamlConfiguration> cache = new ConcurrentHashMap<>();

    /**
     * @param plugin    plugin waarvoor de data wordt opgeslagen
     * @param folderName submap binnen plugin.getDataFolder(), bv. "playerdata"
     */
    public YamlPlayerDataStore(Plugin plugin, String folderName) {
        this.plugin = plugin;
        this.dir = new File(plugin.getDataFolder(), folderName);
        if (!dir.exists() && !dir.mkdirs()) {
            plugin.getLogger().warning("Kon map niet maken: " + dir.getAbsolutePath());
        }
    }

    /**
     * Haal de YamlConfiguration voor deze speler op (uit cache of disk).
     * Wordt niet automatisch gesaved; plugin beslist wanneer.
     */
    public YamlConfiguration getConfig(UUID uuid) {
        return cache.computeIfAbsent(uuid, this::loadFromDisk);
    }

    /**
     * Set een waarde op een bepaald pad voor deze speler (nog niet gesaved).
     */
    public void set(UUID uuid, String path, Object value) {
        YamlConfiguration config = getConfig(uuid);
        config.set(path, value);
    }

    /**
     * Handige helpers voor primitieve types.
     */
    public int getInt(UUID uuid, String path, int def) {
        return getConfig(uuid).getInt(path, def);
    }

    public String getString(UUID uuid, String path, String def) {
        String value = getConfig(uuid).getString(path);
        return value != null ? value : def;
    }

    public boolean getBoolean(UUID uuid, String path, boolean def) {
        return getConfig(uuid).getBoolean(path, def);
    }

    /**
     * Bestaat er al een bestand voor deze speler?
     */
    public boolean hasFile(UUID uuid) {
        return fileOf(uuid).exists();
    }

    /**
     * Save alleen deze speler naar disk.
     */
    public void save(UUID uuid) {
        YamlConfiguration config = cache.get(uuid);
        if (config == null) {
            return; // niets in cache, niets te saven
        }

        File file = fileOf(uuid);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Kon playerdata niet saven voor " + uuid + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save alle spelers in de cache (bv. bij onDisable).
     */
    public void saveAll() {
        for (var entry : cache.entrySet()) {
            UUID uuid = entry.getKey();
            YamlConfiguration config = entry.getValue();

            File file = fileOf(uuid);
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Kon playerdata niet saven voor " + uuid + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Verwijder speler uit cache (optioneel eerst saven).
     */
    public void unload(UUID uuid, boolean save) {
        if (save) {
            save(uuid);
        }
        cache.remove(uuid);
    }

    /**
     * Alleen uit cache gooien, zonder saven.
     */
    public void unload(UUID uuid) {
        unload(uuid, false);
    }

    /* ----------------- interne helpers ----------------- */

    private File fileOf(UUID uuid) {
        return new File(dir, uuid.toString() + ".yml");
    }

    private YamlConfiguration loadFromDisk(UUID uuid) {
        File f = fileOf(uuid);
        if (!f.exists()) {
            return new YamlConfiguration();
        }

        return YamlConfiguration.loadConfiguration(f);
    }
}
