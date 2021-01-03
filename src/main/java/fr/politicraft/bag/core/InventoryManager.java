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
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
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
        Inventory deposit_withdrawal = Bukkit.createInventory(player, 27, exp ? "§eDepôt / Retrait" : "Depôt / Retrait");
        CustomItem customItem = new CustomItem();

        Material itemType = main.getBagInventory().getPreviousClickedItem().get(player).getType();

        if(main.getYmlBag().isFrameItemsEnabled()) inventoryUX.fillGlassPane(deposit_withdrawal, 18);
        inventoryUX.backGlassPane(deposit_withdrawal);

        deposit_withdrawal.setItem(main.getYmlBag().getDepositItemSlot(), customItem.create(Material.GOLD_INGOT, "§eDepôt"));
        deposit_withdrawal.setItem(main.getYmlBag().getDepositWithdrawalItemCounterSlot(), customItem.create(itemType, (exp ? main.getYmlBag().getDepositWithdrawalItemCounterDescription(jsonManager.getExperience(player.getUniqueId())) :  main.getYmlBag().getDepositWithdrawalItemCounterDescription(jsonManager.getItemAmount(player.getUniqueId(), itemType.name())))));
        deposit_withdrawal.setItem(main.getYmlBag().getWithdrawalItemSlot(), customItem.create(Material.GOLD_INGOT, "§eRetrait"));

        player.openInventory(deposit_withdrawal);
    }

    public void backToPreviousInventory(Player player, String previousInv, FileConfiguration configCat, YmlMessage ymlMsg, ItemStack item) {
        switch (previousInv) {
            case "Sac":
                player.performCommand("sac");
                break;
            case "Category":
                itemsInventory(player, configCat, ymlMsg, item);
                break;
        }
    }

    public void sort(Player player, JsonManager jsonManager) throws IOException {

        ItemStack[] hotbar = IntStream.range(0, 9).boxed().map(player.getInventory()::getItem).toArray(ItemStack[]::new);
        ItemStack[] playerInv = jsonManager.isHotbarEnabled(player.getUniqueId()) ? hotbar : player.getInventory().getStorageContents();

        try {
            jsonManager.addAllItems(player.getUniqueId(), playerInv);
        } catch (IOException e) {
            e.printStackTrace();
        }

        main.getBagInventory().getSummarySortedItems().put(player.getName(), playerInv);

        BaseComponent[] hoverInv = new ComponentBuilder(main.getYmlMsg().getSortSummaryHoverMessage()).create();
        HoverEvent hoverEventInv = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverInv);
        ClickEvent clickEventInv = new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sac recap");
        BaseComponent[] message = new ComponentBuilder(main.getYmlMsg().getPrefixMessage() + main.getYmlMsg().getSortSummaryMessage()).append(main.getYmlMsg().getSortSummaryClickableWord()).event(hoverEventInv).event(clickEventInv).create();
        player.spigot().sendMessage(message);
    }



}
