package me.bubbles.yaml;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class YamlDataStore<K> {

    private final Plugin plugin;
    private final File dir;
    private final Function<K, String> keyToFileName;

    private final Map<K, YamlConfiguration> cache = new ConcurrentHashMap<>();

    /**
     * @param plugin       plugin waarvoor de data wordt opgeslagen
     * @param folderName   submap binnen plugin.getDataFolder(), bv. "playerdata" of "islands"
     * @param keyToFileName functie die een key omzet naar een bestandsnaam (zonder .yml)
     */
    public YamlDataStore(Plugin plugin, String folderName, Function<K, String> keyToFileName) {
        this.plugin = plugin;
        this.dir = new File(plugin.getDataFolder(), folderName);
        this.keyToFileName = keyToFileName;

        if (!dir.exists() && !dir.mkdirs()) {
            plugin.getLogger().warning("Kon map niet maken: " + dir.getAbsolutePath());
        }
    }

    /**
     * Haal de YamlConfiguration voor deze key op (uit cache of disk).
     */
    public YamlConfiguration getConfig(K key) {
        return cache.computeIfAbsent(key, this::loadFromDisk);
    }

    /**
     * Set een waarde op een bepaald pad (nog niet automatisch gesaved).
     */
    public void set(K key, String path, Object value) {
        YamlConfiguration config = getConfig(key);
        config.set(path, value);
    }

    public int getInt(K key, String path, int def) {
        return getConfig(key).getInt(path, def);
    }

    public String getString(K key, String path, String def) {
        String value = getConfig(key).getString(path);
        return value != null ? value : def;
    }

    public boolean getBoolean(K key, String path, boolean def) {
        return getConfig(key).getBoolean(path, def);
    }

    /**
     * Bestaat er al een bestand voor deze key?
     */
    public boolean hasFile(K key) {
        return fileOf(key).exists();
    }

    /**
     * Save alleen deze key naar disk.
     */
    public void save(K key) {
        YamlConfiguration config = cache.get(key);
        if (config == null) {
            return; // niets in cache, niets te saven
        }

        File file = fileOf(key);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Kon data niet saven voor key " + key + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Save alle keys in de cache (bv. bij onDisable).
     */
    public void saveAll() {
        for (var entry : cache.entrySet()) {
            K key = entry.getKey();
            YamlConfiguration config = entry.getValue();

            File file = fileOf(key);
            try {
                config.save(file);
            } catch (IOException e) {
                plugin.getLogger().severe("Kon data niet saven voor key " + key + ": " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Verwijder key uit cache (optioneel eerst saven).
     */
    public void unload(K key, boolean save) {
        if (save) {
            save(key);
        }
        cache.remove(key);
    }

    public void unload(K key) {
        unload(key, false);
    }

    /* ----------------- interne helpers ----------------- */

    private File fileOf(K key) {
        String baseName = keyToFileName.apply(key);
        return new File(dir, baseName + ".yml");
    }

    private YamlConfiguration loadFromDisk(K key) {
        File f = fileOf(key);
        if (!f.exists()) {
            return new YamlConfiguration();
        }
        return YamlConfiguration.loadConfiguration(f);
    }
}
