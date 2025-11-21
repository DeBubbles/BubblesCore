package me.bubbles.items;

import me.bubbles.text.ColorUtil;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ItemBuilder {

    private final ItemStack item;
    private final ItemMeta meta;

    private ItemBuilder(Material material, int amount) {
        this.item = new ItemStack(material, amount);
        this.meta = item.getItemMeta();
    }

    // ---------- statische factories ----------

    public static ItemBuilder of(Material material) {
        return new ItemBuilder(material, 1);
    }

    public static ItemBuilder of(Material material, int amount) {
        return new ItemBuilder(material, amount);
    }

    public static ItemBuilder of(XMaterial xMaterial) {
        return new ItemBuilder(xMaterial.getMaterial(), 1);
    }

    public static ItemBuilder of(XMaterial xMaterial, int amount) {
        return new ItemBuilder(xMaterial.getMaterial(), amount);
    }

    // ---------- basis info ----------

    public ItemBuilder name(String name) {
        if (meta == null) return this;
        meta.setDisplayName(ColorUtil.color(name));
        return this;
    }

    public ItemBuilder lore(String... lines) {
        if (meta == null) return this;
        List<String> colored = Arrays.stream(lines)
                .map(ColorUtil::color)
                .collect(Collectors.toList());
        meta.setLore(colored);
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        if (meta == null) return this;
        meta.setLore(ColorUtil.color(lines));
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    // ---------- enchants & flags ----------

    public ItemBuilder enchant(Enchantment enchantment, int level) {
        return enchant(enchantment, level, false);
    }

    public ItemBuilder enchant(Enchantment enchantment, int level, boolean unsafe) {
        if (meta == null) return this;
        meta.addEnchant(enchantment, level, unsafe);
        return this;
    }

    public ItemBuilder clearEnchants() {
        if (meta == null) return this;
        meta.getEnchants().keySet().forEach(meta::removeEnchant);
        return this;
    }

    public ItemBuilder flags(ItemFlag... flags) {
        if (meta == null) return this;
        meta.addItemFlags(flags);
        return this;
    }

    public ItemBuilder unbreakable(boolean unbreakable) {
        if (meta == null) return this;
        meta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Laat het item “glowen” zonder dat de enchant zichtbaar is.
     */
    public ItemBuilder glow(boolean glow) {
        if (meta == null) return this;

        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            // alleen gloweffect weg, niet per se alle enchants als je dat niet wilt
            meta.removeEnchant(Enchantment.DURABILITY);
        }
        return this;
    }

    // ---------- extra ----------

    public ItemBuilder customModelData(Integer data) {
        if (meta == null) return this;
        meta.setCustomModelData(data);
        return this;
    }

    // ---------- build ----------

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        // kopie teruggeven zodat de builder intern "veilig" blijft
        return item.clone();
    }

    public ItemStack buildRaw() {
        // als je bewust de originele stack wilt
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }

    // ---------- TAGS ----------

    public ItemBuilder tagString(String key, String value) {
        TagUtil.setString(item, key, value);
        return this;
    }

    public ItemBuilder tagInt(String key, int value) {
        TagUtil.setInt(item, key, value);
        return this;
    }
}
