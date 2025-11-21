package me.bubbles.items;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public final class TagUtil {

    private static Plugin plugin;

    private TagUtil() {}

    /**
     * Dit moet je één keer aanroepen vanuit je plugin (bijv. in onEnable)
     */
    public static void init(Plugin owningPlugin) {
        plugin = owningPlugin;
    }

    private static NamespacedKey key(String id) {
        if (plugin == null) {
            throw new IllegalStateException("TagUtil is niet geïnitialiseerd! Roep TagUtil.init(plugin) aan in onEnable().");
        }
        return new NamespacedKey(plugin, id);
    }

    private static ItemMeta meta(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta();
    }

    private static PersistentDataContainer pdc(ItemStack item) {
        ItemMeta meta = meta(item);
        return meta != null ? meta.getPersistentDataContainer() : null;
    }


    // ------- PUT -------

    public static void setString(ItemStack item, String id, String value) {
        ItemMeta meta = meta(item);
        if (meta == null) return;
        meta.getPersistentDataContainer().set(key(id), PersistentDataType.STRING, value);
        item.setItemMeta(meta);
    }

    public static void setInt(ItemStack item, String id, int value) {
        ItemMeta meta = meta(item);
        if (meta == null) return;
        meta.getPersistentDataContainer().set(key(id), PersistentDataType.INTEGER, value);
        item.setItemMeta(meta);
    }


    // ------- GET -------

    public static String getString(ItemStack item, String id) {
        PersistentDataContainer container = pdc(item);
        if (container == null) return null;
        return container.get(key(id), PersistentDataType.STRING);
    }

    public static Integer getInt(ItemStack item, String id) {
        PersistentDataContainer container = pdc(item);
        if (container == null) return null;
        return container.get(key(id), PersistentDataType.INTEGER);
    }


    // ------- CHECK -------

    public static boolean has(ItemStack item, String id) {
        PersistentDataContainer container = pdc(item);
        return container != null && container.has(key(id), PersistentDataType.STRING);
    }
}