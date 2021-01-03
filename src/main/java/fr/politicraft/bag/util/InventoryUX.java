package fr.politicraft.bag.util;

import fr.politicraft.bag.Main;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUX {

    private Main main;
    private CustomItem customItem = new CustomItem();

    public InventoryUX(Main main) {
        this.main = main;
    }

    public void fillGlassPane(Inventory inventory, int secondLineStartIndex) {
        ItemStack glassPane = customItem.create(main.getYmlBag().getFrameItem(), " ");
        for(int index = 8; index >= 0; index--) {
            inventory.setItem(index, glassPane);
            inventory.setItem(secondLineStartIndex + index, glassPane);
        }
    }

    public void backGlassPane(Inventory inventory) {
        ItemStack glassPane = customItem.create(main.getYmlBag().getBackButtonItem(), main.getYmlBag().getBackButtonName());
        inventory.setItem(main.getYmlBag().getBackButtonSlot(), glassPane);
    }

}
