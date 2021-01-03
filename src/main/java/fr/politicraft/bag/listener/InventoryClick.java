package fr.politicraft.bag.listener;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.config.YmlMessage;
import fr.politicraft.bag.core.InventoryManager;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.util.CustomItem;
import fr.politicraft.bag.util.InventoryUX;
import fr.politicraft.bag.util.YmlFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

public class InventoryClick implements Listener {

    private Main main;
    private CustomItem customItem;
    private InventoryUX inventoryUX;
    private InventoryManager inventoryManager;
    private YmlMessage ymlMsg;
    private JsonManager jsonManager;

    public InventoryClick(Main main) {
        this.main = main;
        this.customItem = new CustomItem();
        this.inventoryUX = new InventoryUX(main);
        this.inventoryManager = new InventoryManager(main);
        this.ymlMsg = new YmlMessage(main);
        this.jsonManager = new JsonManager(main);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) throws IOException {
        ItemStack item = e.getCurrentItem();
        Player player = (Player) e.getWhoClicked();
        boolean isLeftClick = e.isLeftClick();
        boolean isRightClick = e.isRightClick();
        InventoryView view = player.getOpenInventory();

        if(item == null || e.getClickedInventory() == view.getBottomInventory()) return;

        FileConfiguration configCat = YamlConfiguration.loadConfiguration(new File(main.mainPath, YmlFile.CATEGORIES));
        String title = e.getView().getTitle();
        switch (title) {
            case "Sac":
                e.setCancelled(true);
                switch (item.getType()) {
                    case EXPERIENCE_BOTTLE:
                        inventoryManager.experience(player, item);
                        inventoryManager.amountInventory(player, jsonManager, item, true);
                        break;
                    case GRAY_STAINED_GLASS_PANE:
                        break;
                    case ENDER_CHEST:
                        if(isRightClick) {
                            jsonManager.updateHotbarConfig(player.getUniqueId());
                            String symbol = (jsonManager.isHotbarEnabled(player.getUniqueId()) ? "§a✔" : "§c❌");
                            e.getClickedInventory().setItem(e.getSlot(), customItem.create("§eTrie Automatique", Material.ENDER_CHEST,
                                    main.getYmlBag().getAutomaticSortDescription(symbol)));
                        } else if(isLeftClick) {
                            inventoryManager.sort(player, jsonManager);
                        }
                        break;
                    default:
                        inventoryManager.itemsInventory(player, configCat, ymlMsg, item);
                        break;
                }
                break;
            case "Depôt / Retrait":
                if(item.getType() == Material.RED_STAINED_GLASS_PANE) inventoryManager.backToPreviousInventory(player, "Category", configCat, ymlMsg, customItem.create(main.getBagInventory().getPreviousClickedItem().get(player).getType(), main.getBagInventory().getCategoryInventoryName().get(player)));

                String itemName = item.getItemMeta().getDisplayName();
                e.setCancelled(true);
                player.updateInventory();
                switch (itemName) {
                    case "§eDepôt":
                        inventoryManager.goldInteraction(player, main.getBagInventory().getDeposit(), ymlMsg.getPrefixMessage() + ymlMsg.getDepositRequestMessage());
                        break;

                    case "§eRetrait":
                        inventoryManager.goldInteraction(player, main.getBagInventory().getWithdrawal(), ymlMsg.getPrefixMessage() + ymlMsg.getWithdrawalRequestMessage());
                        break;
                }
                break;
                // Experience inventory
            case "§eDepôt / Retrait":
                if(item.getType() == Material.RED_STAINED_GLASS_PANE) inventoryManager.backToPreviousInventory(player, "Sac", null, null, null);

                String itemName_ = item.getItemMeta().getDisplayName();
                e.setCancelled(true);
                player.updateInventory();
                switch (itemName_) {
                    case "§eDepôt":
                        inventoryManager.goldInteraction(player, main.getBagInventory().getExpDeposit(), ymlMsg.getPrefixMessage() + ymlMsg.getExpDepositRequestMessage());
                        break;

                    case "§eRetrait":
                        inventoryManager.goldInteraction(player, main.getBagInventory().getExpWithdrawal(), ymlMsg.getPrefixMessage() + ymlMsg.getExpWithdrawalRequestMessage());
                        break;
                }
                break;
            case "Trie Récap":
            case "Blacklist":
                e.setCancelled(true);
                break;
        }
        // Items Inventory
        if(title.equalsIgnoreCase(main.getBagInventory().getCategoryInventoryName().get(player))) {
            switch (item.getType()) {
                case RED_STAINED_GLASS_PANE:
                    inventoryManager.backToPreviousInventory(player, "Sac", null, null, null);
                    break;
                default:
                    JsonManager jsonManager = new JsonManager(main);
                    if(isLeftClick) {
                        inventoryManager.amountInventory(player, jsonManager, item, false);
                    } else if(isRightClick) {
                        jsonManager.updateBlacklistedItem(player.getUniqueId(), item.getType().name());

                        boolean isBlacklisted = jsonManager.blacklistItemExist(player.getUniqueId(), item.getType().toString());
                        String symbol = (isBlacklisted ? "§a✔" : "§c❌");
                        int amount = jsonManager.getItemAmount(player.getUniqueId(), item.getType().name());

                        e.getClickedInventory().setItem(e.getSlot(), customItem.create(
                                Material.valueOf(item.getType().toString()),
                                main.getYmlBag().getItemsDescription(amount, symbol)));
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

        if(title.equalsIgnoreCase(main.getYmlBag().getInventoryMenuName())) {
            e.setCancelled(true);
        }
    }

}
