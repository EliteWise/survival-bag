package fr.politicraft.bag.core;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.util.CustomItem;
import fr.politicraft.bag.util.InventoryUX;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryManager {

    private Main main;
    private CustomItem customItem;
    private InventoryUX inventoryUX;

    public InventoryManager(Main main) {
        this.main = main;
        this.customItem = new CustomItem();
        this.inventoryUX = new InventoryUX();
    }

    public void itemsInventory(Player player, FileConfiguration config, ItemStack item) {
        Inventory category = Bukkit.createInventory(player, 54, item.getItemMeta().getDisplayName());
        AtomicInteger i = new AtomicInteger(0);

        config.getConfigurationSection("Bag").getKeys(false).forEach(c -> {

            List<String> categoryItems = new ArrayList<>();
            String inventoryName = config.getString("Bag." + c + ".DisplayName");

            JsonManager jsonManager = new JsonManager(main);

            if(inventoryName.equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
                categoryItems = config.getStringList("Bag." + c + ".Items");
                main.getBagInventory().getCategoryInventoryName().put(player, inventoryName);
                main.getBagInventory().getPreviousClickedItem().put(player, item);
            }

            inventoryUX.backGlassPane(category);

            for(Object categoryItem : categoryItems) {
                try {
                    boolean isBlacklisted = jsonManager.blacklistItemExist(player.getUniqueId(), categoryItem.toString().toUpperCase());
                    category.setItem(9 + i.getAndIncrement(), customItem.create(
                            Material.valueOf(categoryItem.toString().toUpperCase()),
                            "§7-----------",
                            "§7Total: §e" + jsonManager.getItemAmount(player.getUniqueId(), categoryItem.toString().toUpperCase()),
                            "§7Blacklist: " + (isBlacklisted ? "§a✔" : "§c❌"),
                            "§7-----------",
                            "§aClic gauche: déposer/retirer l'item.",
                            "§cClic droit: ajouter/retirer de la blacklist."));
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
        player.openInventory(category);
    }

    public void goldInteraction(Player player, ArrayList<String> list, String message) {
        player.closeInventory();
        list.add(player.getName());
        player.sendMessage(message);
        Bukkit.getScheduler().runTaskLater(main, new Runnable() {
            @Override
            public void run() {
                list.remove(player.getName());
            }
        }, 100);
    }

    public void experience(Player player, ItemStack item) {
        main.getBagInventory().getCategoryInventoryName().put(player, "exp");
        main.getBagInventory().getPreviousClickedItem().put(player, item);
    }

    public void amountInventory(Player player, JsonManager jsonManager, ItemStack currentItem, boolean exp) throws IOException {
        main.getBagInventory().getPreviousClickedItem().put(player, currentItem);
        Inventory deposit_withdrawal = Bukkit.createInventory(player, 27, exp ? "§eDepôt / Retrait" : "Depôt / Retrait");
        CustomItem customItem = new CustomItem();

        Material itemType = main.getBagInventory().getPreviousClickedItem().get(player).getType();

        inventoryUX.fillGlassPane(deposit_withdrawal, 18);
        inventoryUX.backGlassPane(deposit_withdrawal);

        deposit_withdrawal.setItem(11, customItem.create(Material.GOLD_INGOT, "§eDepôt"));
        deposit_withdrawal.setItem(13, customItem.create(itemType, exp ? (int) jsonManager.getExperience(player.getUniqueId()) : jsonManager.getItemAmount(player.getUniqueId(), itemType.name())));
        deposit_withdrawal.setItem(15, customItem.create(Material.GOLD_INGOT, "§eRetrait"));

        player.openInventory(deposit_withdrawal);
    }

    public void backToPreviousInventory(Player player, String previousInv, FileConfiguration config, ItemStack item) {
        switch (previousInv) {
            case "Sac":
                player.performCommand("sac");
                break;
            case "Category":
                itemsInventory(player, config, item);
                break;
        }
    }

}
