package me.bubbles.configwrapper;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

/**
 * Lichte wrapper rond een YAML-configbestand in de pluginmap.
 *
 * - Automatisch aanmaken van het bestand (optioneel met defaults uit resources)
 * - Makkelijke accessors voor get/set
 * - save() en reload() helpers
 */
public final class ConfigWrapper {

    private final Plugin plugin;
    private final File file;
    private FileConfiguration config;

    /**
     * Laadt een configbestand in de pluginmap.
     * Als het bestand nog niet bestaat, wordt eerst geprobeerd het uit de jar-resources te kopiëren.
     * Bestaat er geen resource met dezelfde naam, dan wordt een leeg bestand aangemaakt.
     *
     * @param plugin   plugin instance
     * @param fileName bestandsnaam, bv. "config.yml" of "settings.yml"
     */
    public static ConfigWrapper load(Plugin plugin, String fileName) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            plugin.getLogger().warning("Kon pluginmap niet maken: " + dataFolder.getAbsolutePath());
        }

        File file = new File(dataFolder, fileName);

        if (!file.exists()) {
            // Probeer eerst resource te kopiëren
            try {
                plugin.saveResource(fileName, false);
            } catch (IllegalArgumentException ex) {
                // Geen resource met die naam, maak leeg bestand
                try {
                    if (!file.createNewFile()) {
                        plugin.getLogger().warning("Kon configbestand niet maken: " + file.getAbsolutePath());
                    }
                } catch (IOException io) {
                    plugin.getLogger().severe("Fout bij maken van configbestand: " + io.getMessage());
                    io.printStackTrace();
                }
            }
        }

        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        return new ConfigWrapper(plugin, file, cfg);
    }

    private ConfigWrapper(Plugin plugin, File file, FileConfiguration config) {
        this.plugin = plugin;
        this.file = file;
        this.config = config;
    }

    /**
     * @return onderliggende FileConfiguration (voor geavanceerde use-cases)
     */
    public FileConfiguration getConfig() {
        return config;
    }

    /* -------------------- Convenience getters -------------------- */

    public String getString(String path) {
        return config.getString(path);
    }

    public String getString(String path, String def) {
        String value = config.getString(path);
        return value != null ? value : def;
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public double getDouble(String path, double def) {
        return config.getDouble(path, def);
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    /* -------------------- Setters -------------------- */

    public void set(String path, Object value) {
        config.set(path, value);
    }

    /* -------------------- I/O helpers -------------------- */

    /**
     * Schrijft de huidige config naar disk.
     */
    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Kon config niet saven naar " + file.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Laadt de config opnieuw vanaf disk.
     */
    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * @return het fysieke bestand op disk.
     */
    public File getFile() {
        return file;
    }
}
