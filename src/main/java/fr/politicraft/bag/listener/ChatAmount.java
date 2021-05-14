package fr.politicraft.bag.listener;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.config.YmlMessage;
import fr.politicraft.bag.core.ChatRequestManager;
import fr.politicraft.bag.core.InventoryManager;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.model.BagInventory;
import fr.politicraft.bag.util.CustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.UUID;

public class ChatAmount implements Listener {

    private Main main;
    private InventoryManager inventoryManager;
    private CustomItem customItem;
    private BagInventory bag;
    private YmlMessage ymlMsg;

    public ChatAmount(Main main) {
        this.main = main;
        this.inventoryManager = new InventoryManager(main);
        this.customItem = new CustomItem();
        this.bag = main.getBagInventory();
        this.ymlMsg = new YmlMessage(main);
    }

    public void reopenAmountInventory(Player chatPlayer, JsonManager jsonManager, Material previousClickedItem, boolean exp) {
        Bukkit.getScheduler().runTask(main, () -> {
            // Your code to be run on the main thread here, like opening an inventory for the player etc.
            try {
                inventoryManager.amountInventory(chatPlayer, jsonManager, new ItemStack(previousClickedItem), exp);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onUseChat(AsyncPlayerChatEvent e) throws IOException {
        Player chatPlayer = e.getPlayer();
        String playerName = chatPlayer.getName();

        if(!(bag.getDeposit().contains(playerName) || bag.getWithdrawal().contains(playerName) || bag.getExpDeposit().contains(playerName) || bag.getExpWithdrawal().contains(playerName))) return;

        String msg = e.getMessage();
        int amount;

        if(msg.equalsIgnoreCase("all")) {
            amount = -1;
        } else {
            try {
                amount = Integer.parseInt(msg);
            } catch (NumberFormatException ex) {
                chatPlayer.sendMessage(ymlMsg.getPrefixMessage() + ymlMsg.getInvalidNumberMessage());
                e.setCancelled(true);
                return;
            }
        }

        UUID playerUUID = chatPlayer.getUniqueId();
        Inventory playerInv = chatPlayer.getInventory();
        JsonManager jsonManager = new JsonManager(main);

        Material previousClickedItem = bag.getPreviousClickedItem().get(chatPlayer).getType();
        ChatRequestManager chatRequestManager = new ChatRequestManager(main);

        if(bag.getDeposit().contains(playerName)) {

            String itemsLimitChar = chatPlayer.getEffectivePermissions().stream().filter(perm -> perm.getPermission().contains("survivalbag.items")).findFirst().get().getPermission();
            String itemsLimitNbr = itemsLimitChar.replaceAll("[^0-9]", "");

            if(itemsLimitNbr.isEmpty()) itemsLimitNbr = String.valueOf(main.getYmlPerm().getItemsDefaultValue());

            if((jsonManager.summarizeStoredItems(playerUUID) + amount <= Integer.parseInt(itemsLimitNbr))) {
                chatRequestManager.depositItems(chatPlayer, playerUUID, playerInv, previousClickedItem, amount, bag, jsonManager, ymlMsg);
                reopenAmountInventory(chatPlayer, jsonManager, previousClickedItem, false);
            } else {
                chatPlayer.sendMessage(main.getYmlMsg().getItemsLimitExceededMessage());
                chatRequestManager.depositItems(chatPlayer, playerUUID, playerInv, previousClickedItem, (Integer.parseInt(itemsLimitNbr) - jsonManager.getItemAmount(playerUUID, previousClickedItem.toString())), bag, jsonManager, ymlMsg);
            }
        } else if(bag.getWithdrawal().contains(playerName)) {

            chatRequestManager.withdrawalItems(chatPlayer, playerUUID, playerInv, previousClickedItem, amount, bag, jsonManager, ymlMsg);
            reopenAmountInventory(chatPlayer, jsonManager, previousClickedItem, false);

        } else if(bag.getExpDeposit().contains(playerName)) {

            String expLimitChar = chatPlayer.getEffectivePermissions().stream().filter(perm -> perm.getPermission().contains("survivalbag.xp")).findFirst().get().getPermission();
            String expLimitNbr = expLimitChar.replaceAll("[^0-9]", "");

            if(expLimitNbr.isEmpty()) expLimitNbr = String.valueOf(main.getYmlPerm().getXpDefaultValue());

            if(jsonManager.getExperience(playerUUID) + amount <= Integer.parseInt(expLimitNbr)) {
                chatRequestManager.depositExp(chatPlayer, playerUUID, amount, bag, jsonManager, ymlMsg);
                reopenAmountInventory(chatPlayer, jsonManager, previousClickedItem, true);
            } else {
                chatPlayer.sendMessage(main.getYmlMsg().getXpLimitExceededMessage());
                chatRequestManager.depositExp(chatPlayer, playerUUID, (Integer.parseInt(expLimitNbr) - jsonManager.getExperience(playerUUID)), bag, jsonManager, ymlMsg);
            }
        } else if(bag.getExpWithdrawal().contains(playerName)) {

            chatRequestManager.withdrawalExp(chatPlayer, playerUUID, amount, bag, jsonManager, ymlMsg);
            reopenAmountInventory(chatPlayer, jsonManager, previousClickedItem, true);

        }
        e.setCancelled(true);
    }
}
