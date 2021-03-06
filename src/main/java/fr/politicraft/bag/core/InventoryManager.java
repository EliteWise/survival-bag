package fr.politicraft.bag.core;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.config.YmlMessage;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.util.CustomItem;
import fr.politicraft.bag.util.InventoryUX;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class InventoryManager {

    private Main main;
    private CustomItem customItem;
    private InventoryUX inventoryUX;

    public InventoryManager(Main main) {
        this.main = main;
        this.customItem = new CustomItem();
        this.inventoryUX = new InventoryUX(main);
    }

    public void itemsInventory(Player player, FileConfiguration configCat, YmlMessage ymlMsg, ItemStack item) {
        Inventory category = Bukkit.createInventory(player, 54, item.getItemMeta().getDisplayName());
        AtomicInteger i = new AtomicInteger(0);

        configCat.getConfigurationSection("Bag").getKeys(false).forEach(c -> {

            List<String> categoryItems = new ArrayList<>();
            String inventoryName = configCat.getString("Bag." + c + ".DisplayName");

            JsonManager jsonManager = new JsonManager(main);

            if(inventoryName.equalsIgnoreCase(item.getItemMeta().getDisplayName())) {
                categoryItems = configCat.getStringList("Bag." + c + ".Items");
                main.getBagInventory().getCategoryInventoryName().put(player, inventoryName);
                main.getBagInventory().getPreviousClickedItem().put(player, item);
            }

            inventoryUX.backGlassPane(category);

            try {
                if(!jsonManager.getItemsOwnedVisibilityConfig(player.getUniqueId())) {
                    for(Object categoryItem : categoryItems) {
                        try {
                            boolean isBlacklisted = jsonManager.blacklistItemExist(player.getUniqueId(), categoryItem.toString().toUpperCase());
                            String symbol = (isBlacklisted ? "§a✔" : "§c❌");
                            int amount = jsonManager.getItemAmount(player.getUniqueId(), categoryItem.toString().toUpperCase());

                            category.setItem(9 + i.getAndIncrement(), customItem.create(
                                    Material.valueOf(categoryItem.toString().toUpperCase()),
                                    main.getYmlBag().getItemsDescription(amount, symbol)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // Show only items owned
                    for(Object categoryItem : categoryItems) {
                        try {
                            boolean isBlacklisted = jsonManager.blacklistItemExist(player.getUniqueId(), categoryItem.toString().toUpperCase());
                            String symbol = (isBlacklisted ? "§a✔" : "§c❌");
                            int amount = jsonManager.getItemAmount(player.getUniqueId(), categoryItem.toString().toUpperCase());

                            if(jsonManager.getItemsOwned(player.getUniqueId()).contains(categoryItem.toString().toUpperCase())) {
                                category.setItem(9 + i.getAndIncrement(), customItem.create(
                                        Material.valueOf(categoryItem.toString().toUpperCase()),
                                        main.getYmlBag().getItemsDescription(amount, symbol)));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
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
        }, main.getYmlMsg().getTimingToWriteAmount() * 20);
    }

    public void experience(Player player, ItemStack item) {
        main.getBagInventory().getCategoryInventoryName().put(player, "exp");
        main.getBagInventory().getPreviousClickedItem().put(player, item);
    }

    public void amountInventory(Player player, JsonManager jsonManager, ItemStack currentItem, boolean exp) throws IOException {
        main.getBagInventory().getPreviousClickedItem().put(player, currentItem);
        Inventory deposit_withdrawal = Bukkit.createInventory(player, 27, exp ? main.getYmlBag().getInventoryExperienceName() : main.getYmlBag().getInventoryDepositWithdrawalName());
        CustomItem customItem = new CustomItem();

        Material itemType = main.getBagInventory().getPreviousClickedItem().get(player).getType();

        if(main.getYmlBag().isFrameItemsEnabled()) inventoryUX.fillGlassPane(deposit_withdrawal, 18);
        inventoryUX.backGlassPane(deposit_withdrawal);

        deposit_withdrawal.setItem(main.getYmlBag().getDepositItemSlot(), customItem.create(main.getYmlBag().getDepositItem(), main.getYmlBag().getDepositItemName()));
        deposit_withdrawal.setItem(main.getYmlBag().getDepositWithdrawalItemCounterSlot(), customItem.create(itemType, (exp ? main.getYmlBag().getDepositWithdrawalItemCounterDescription(jsonManager.getExperience(player.getUniqueId())) :  main.getYmlBag().getDepositWithdrawalItemCounterDescription(jsonManager.getItemAmount(player.getUniqueId(), itemType.name())))));
        deposit_withdrawal.setItem(main.getYmlBag().getWithdrawalItemSlot(), customItem.create(main.getYmlBag().getWithdrawalItem(), main.getYmlBag().getWithdrawalItemName()));

        player.openInventory(deposit_withdrawal);
    }

    public void backToPreviousInventory(Player player, String previousInv, FileConfiguration configCat, YmlMessage ymlMsg, ItemStack item) {
        if(previousInv.equalsIgnoreCase(main.getYmlBag().getInventoryMenuName())) {
            player.performCommand("sac");
        } else if(previousInv.equalsIgnoreCase("Category")) {
            itemsInventory(player, configCat, ymlMsg, item);
        } else {
            player.performCommand("sac");
        }
    }

    public List<ItemStack> getSortSpecialItems(ItemStack[] items) {
        return Arrays.stream(items).filter(item -> (item != null) && (item.getType() == Material.ENCHANTED_BOOK || EnchantmentTarget.TOOL.includes(item)
                || EnchantmentTarget.WEAPON.includes(item) || EnchantmentTarget.ARMOR.includes(item)
                || item.getType().toString().contains("SHULKER") || item.getType() == Material.POTION
                || item.getType() == Material.SPLASH_POTION)).collect(Collectors.toList());
    }

    public void filterSortSpecialItems(UUID playerUUID, ItemStack[] items) {
        JsonManager jsonManager = new JsonManager(main);

        getSortSpecialItems(items).forEach((item) -> {
            try {
                jsonManager.addSpecialItem(playerUUID, item);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public ItemStack[] substractItemsArray(ItemStack[] basicItems) {
        return Arrays.stream(basicItems).filter(item -> !getSortSpecialItems(basicItems).contains(item)).toArray(ItemStack[]::new);
    }

    public void sort(Player player, JsonManager jsonManager) throws IOException {

        UUID playerUUID = player.getUniqueId();
        String itemsLimitChar = player.getEffectivePermissions().stream().filter(perm -> perm.getPermission().contains("survivalbag.items")).findFirst().get().getPermission();
        String itemsLimitNbr = itemsLimitChar.replaceAll("[^0-9]", "");

        if(itemsLimitNbr.isEmpty()) itemsLimitNbr = String.valueOf(main.getYmlPerm().getItemsDefaultValue());

        ItemStack[] playerInv = player.getInventory().getStorageContents();
        ItemStack[] hotbar = IntStream.range(0, 9).boxed().map(player.getInventory()::getItem).toArray(ItemStack[]::new);
        ItemStack[] exceptHotbar = IntStream.range(9, 36).boxed().map(player.getInventory()::getItem).toArray(ItemStack[]::new);

        try {
            switch (jsonManager.getSortConfig(player.getUniqueId())) {
                case "all":

                    int amount = Arrays.stream(playerInv).filter(Objects::nonNull).map(ItemStack::getAmount).mapToInt(Integer::intValue).sum();
                    int nbrItemsToRemove = (jsonManager.summarizeStoredItems(playerUUID) + amount) - Integer.parseInt(itemsLimitNbr);

                    if(jsonManager.summarizeStoredItems(playerUUID) + amount > Integer.parseInt(itemsLimitNbr)) {
                        player.sendMessage(main.getYmlMsg().getSortedExtraItemsToRemoveMessage(nbrItemsToRemove));
                        return;
                    }

                    filterSortSpecialItems(playerUUID, playerInv);
                    jsonManager.addAllItems(player.getUniqueId(), substractItemsArray(playerInv), itemsLimitNbr);
                    main.getBagInventory().getSummarySortedItems().put(player.getName(), playerInv);
                    break;
                case "hotbar-only":
                    filterSortSpecialItems(playerUUID, hotbar);
                    jsonManager.addAllItems(player.getUniqueId(), substractItemsArray(hotbar), itemsLimitNbr);
                    main.getBagInventory().getSummarySortedItems().put(player.getName(), hotbar);
                    break;
                case "all-except-hotbar":
                    filterSortSpecialItems(playerUUID, exceptHotbar);
                    jsonManager.addAllItems(player.getUniqueId(), substractItemsArray(exceptHotbar), itemsLimitNbr);
                    main.getBagInventory().getSummarySortedItems().put(player.getName(), exceptHotbar);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        BaseComponent[] hoverInv = new ComponentBuilder(main.getYmlMsg().getSortSummaryHoverMessage()).create();
        HoverEvent hoverEventInv = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverInv);
        ClickEvent clickEventInv = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sac recap");
        BaseComponent[] message = new ComponentBuilder(main.getYmlMsg().getPrefixMessage() + main.getYmlMsg().getSortSummaryMessage()).append(main.getYmlMsg().getSortSummaryClickableWord()).event(hoverEventInv).event(clickEventInv).create();
        player.spigot().sendMessage(message);
    }

    public void specialInventory(Player player) throws IOException {
        Inventory specialInv = Bukkit.createInventory(player, 54, main.getYmlBag().getSpecialInventoryName());

        // Display special items
        JsonManager jsonManager = new JsonManager(main);
        player.openInventory(jsonManager.loadSpecialInventory(player.getUniqueId(), specialInv));
    }

}
