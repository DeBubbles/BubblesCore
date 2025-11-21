package me.bubbles.gui;

import me.bubbles.text.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class GUI implements InventoryHolder {

    protected final Player viewer;
    protected final Inventory inventory;

    protected GUI(Player viewer, int size, String title) {
        this.viewer = viewer;
        this.inventory = Bukkit.createInventory(this, size, ColorUtil.color(title));
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
    public Player getViewer() {
        return viewer;
    }

    protected abstract void setupItems();

    public abstract void onClick(InventoryClickEvent event);

    public void onClose(InventoryCloseEvent event) {
        // optioneel
    }

    public void open() {
        setupItems();
        viewer.openInventory(inventory);
    }
}
