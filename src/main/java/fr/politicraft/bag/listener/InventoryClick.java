package fr.politicraft.bag.listener;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.core.InventoryManager;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.util.CustomItem;
import fr.politicraft.bag.util.InventoryUX;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class InventoryClick implements Listener {

    private Main main;
    private CustomItem customItem;
    private InventoryUX inventoryUX;
    private InventoryManager inventoryManager;

    public InventoryClick(Main main) {
        this.main = main;
        this.customItem = new CustomItem();
        this.inventoryUX = new InventoryUX();
        this.inventoryManager = new InventoryManager(main);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws IOException {
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();
        boolean isLeftClick = e.isLeftClick();
        boolean isRightClick = e.isRightClick();
        FileConfiguration config = main.getConfig();
        InventoryView view = player.getOpenInventory();

        if(item == null || e.getClickedInventory() == view.getBottomInventory()) return;

        String title = e.getView().getTitle();

        switch (title) {
            case "Sac":
                e.setCancelled(true);
                switch (item.getType()) {
                    case EXPERIENCE_BOTTLE:
                        JsonManager jsonManager = new JsonManager(main);
                        inventoryManager.experience(player, item);
                        inventoryManager.amountInventory(player, jsonManager, item, true);
                        break;
                    case GRAY_STAINED_GLASS_PANE:
                        break;
                    default:
                        inventoryManager.itemsInventory(player, config, item);
                        break;
                }
                break;
            case "Depôt / Retrait":
                if(item.getType() == Material.RED_STAINED_GLASS_PANE) inventoryManager.backToPreviousInventory(player, "Category", config, customItem.create(main.getBagInventory().getPreviousClickedItem().get(player).getType(), main.getBagInventory().getCategoryInventoryName().get(player)));

                String itemName = item.getItemMeta().getDisplayName();
                e.setCancelled(true);
                player.updateInventory();
                switch (itemName) {
                    case "§eDepôt":
                        inventoryManager.goldInteraction(player, main.getBagInventory().getDeposit(), "§eEntrez la somme d'items que vous voulez déposer.");
                        break;

                    case "§eRetrait":
                        inventoryManager.goldInteraction(player, main.getBagInventory().getWithdrawal(), "§eEntrez la somme d'items que vous voulez retirer.");
                        break;
                }
                break;
                // Experience inventory
            case "§eDepôt / Retrait":
                if(item.getType() == Material.RED_STAINED_GLASS_PANE) inventoryManager.backToPreviousInventory(player, "Sac", null, null);

                String itemName_ = item.getItemMeta().getDisplayName();
                e.setCancelled(true);
                player.updateInventory();
                switch (itemName_) {
                    case "§eDepôt":
                        inventoryManager.goldInteraction(player, main.getBagInventory().getExpDeposit(), "§7[§eSac§7] §eEntrez la somme d'experience que vous voulez déposer.");
                        break;

                    case "§eRetrait":
                        inventoryManager.goldInteraction(player, main.getBagInventory().getExpWithdrawal(), "§7[§eSac§7] §eEntrez la somme d'experience que vous voulez retirer.");
                        break;
                }
                break;
        }
        // Items Inventory
        if(title.equalsIgnoreCase(main.getBagInventory().getCategoryInventoryName().get(player))) {
            switch (item.getType()) {
                case RED_STAINED_GLASS_PANE:
                    inventoryManager.backToPreviousInventory(player, "Sac", null, null);
                    break;
                default:
                    JsonManager jsonManager = new JsonManager(main);
                    if(isLeftClick) {
                        inventoryManager.amountInventory(player, jsonManager, item, false);
                    } else if(isRightClick) {
                        jsonManager.updateBlacklistedItem(player.getUniqueId(), item.getType().name());

                        boolean isBlacklisted = jsonManager.blacklistItemExist(player.getUniqueId(), item.getType().toString());

                        e.getClickedInventory().setItem(e.getSlot(), customItem.create(
                                Material.valueOf(item.getType().toString()),
                                "§7-----------",
                                "§7Total: §e" + jsonManager.getItemAmount(player.getUniqueId(), item.getType().name()),
                                "§7Blacklist: " + (isBlacklisted ? "§a✔" : "§c❌"),
                                "§7-----------",
                                "§aClic gauche: déposer/retirer l'item.",
                                "§cClic droit: ajouter/retirer de la blacklist."));
                    }
                    break;
            }
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryDragEvent e) {
        InventoryView inventoryView = e.getView();
        String title = inventoryView.getTitle();

        switch (title) {
            case "Sac":
            case "§eDepôt / Retrait":
                e.setCancelled(true);
                break;
        }
    }

}
