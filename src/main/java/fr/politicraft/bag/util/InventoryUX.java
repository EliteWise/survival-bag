package fr.politicraft.bag.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUX {

    private CustomItem customItem = new CustomItem();

    public InventoryUX() {

    }

    public void fillGlassPane(Inventory inventory, int secondLineStartIndex) {
        ItemStack glassPane = customItem.create(Material.GRAY_STAINED_GLASS_PANE, " ");
        for(int index = 8; index >= 0; index--) {
            inventory.setItem(index, glassPane);
            inventory.setItem(secondLineStartIndex + index, glassPane);
        }
    }

    public void backGlassPane(Inventory inventory) {
        ItemStack glassPane = customItem.create(Material.RED_STAINED_GLASS_PANE, "§cRetour");
        inventory.setItem(0, glassPane);
    }

}
