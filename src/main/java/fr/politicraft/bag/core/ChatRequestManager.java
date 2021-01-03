package fr.politicraft.bag.core;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.config.YmlMessage;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.model.BagInventory;
import fr.politicraft.bag.util.Experience;
import me.pikamug.localelib.LocaleManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

public class ChatRequestManager {

    private Main main;

    public ChatRequestManager(Main main) {
        this.main = main;
    }

    public void sendTranslatedMessage(Player player, String message, Material material) {
        Bukkit.getScheduler().runTask(main, () -> {
            LocaleManager localeManager = new LocaleManager();
            localeManager.sendMessage(player, main.getYmlMsg().getPrefixMessage() + message, material, (short) 0, null);
        });
    }

    public void sendMessage(Player player, String message) {
        player.sendMessage(main.getYmlMsg().getPrefixMessage() + message);
    }

    public void depositItems(Player player, UUID playerUUID, Inventory playerInv, Material previousClickedItem, int amountRequested, BagInventory bag, JsonManager jsonManager, YmlMessage ymlMsg) throws IOException {
        if(playerInv.contains(previousClickedItem)) {

            int itemAmountOwned = 0;

            for(ItemStack itemStack : playerInv) {
                if(itemStack != null && itemStack.getType() == previousClickedItem) {
                    itemAmountOwned += itemStack.getAmount();
                }
            }

            if(amountRequested == -1) amountRequested = itemAmountOwned;

            if(itemAmountOwned >= amountRequested) {
                jsonManager.addItemAmount(playerUUID, previousClickedItem.name(), amountRequested);
                playerInv.removeItem(new ItemStack(previousClickedItem, amountRequested));

                sendTranslatedMessage(player, ymlMsg.getDepositTransactionMessage(amountRequested), previousClickedItem);
            } else {
                jsonManager.addItemAmount(playerUUID, previousClickedItem.name(), itemAmountOwned);
                playerInv.removeItem(new ItemStack(previousClickedItem, itemAmountOwned));

                sendTranslatedMessage(player, ymlMsg.getDepositTransactionAdjustedMessage(itemAmountOwned), previousClickedItem);
            }

        } else {
            // The player doesn't own this item //
            sendTranslatedMessage(player, ymlMsg.getDepositTransactionErrorMessage(amountRequested), previousClickedItem);
        }
        bag.getDeposit().remove(player.getName());
    }

    public void withdrawalItems(Player player, UUID playerUUID, Inventory playerInv, Material previousClickedItem, int amountRequested, BagInventory bag, JsonManager jsonManager, YmlMessage ymlMsg) throws IOException {
        int itemAmountOwned = jsonManager.getItemAmount(playerUUID, previousClickedItem.name());

        if(amountRequested == -1) amountRequested = itemAmountOwned;

        if(itemAmountOwned == 0) {
            sendMessage(player, ymlMsg.getWithdrawalTransactionErrorMessage(amountRequested));
            return;
        }

        if(itemAmountOwned >= amountRequested) {
            Collection<ItemStack> extraItems = playerInv.addItem(new ItemStack(previousClickedItem, amountRequested)).values();
            if(extraItems.size() == 0) {
                jsonManager.removeItemAmount(player.getUniqueId(), previousClickedItem.toString(), amountRequested);
                sendTranslatedMessage(player, ymlMsg.getWithdrawalTransactionMessage(amountRequested), previousClickedItem);
            } else {
                int extraAmount = extraItems.stream().filter(item -> item.getAmount() > 0).findFirst().get().getAmount();

                jsonManager.removeItemAmount(player.getUniqueId(), previousClickedItem.toString(), amountRequested - extraAmount);

                sendTranslatedMessage(player, ymlMsg.getWithdrawalTransactionMessage(amountRequested - extraAmount), previousClickedItem);
            }
        } else {
            jsonManager.removeItemAmount(playerUUID, previousClickedItem.name(), itemAmountOwned);
            playerInv.addItem(new ItemStack(previousClickedItem, itemAmountOwned));

            sendTranslatedMessage(player, ymlMsg.getWithdrawalTransactionAdjustedMessage(itemAmountOwned), previousClickedItem);
        }
        bag.getWithdrawal().remove(player.getName());
    }

    public void depositExp(Player player, UUID playerUUID, int amountRequested, BagInventory bag, JsonManager jsonManager, YmlMessage ymlMsg) throws IOException {
        int exp = Experience.getPlayerExp(player);

        if(amountRequested == -1) amountRequested = exp;

        if(exp == 0) {
            sendMessage(player, ymlMsg.getPrefixMessage() + ymlMsg.getExpDepositTransactionErrorMessage(amountRequested));
            return;
        }

        if(exp >= amountRequested) {
            jsonManager.addExperience(playerUUID, amountRequested);
            Experience.changePlayerExp(player, -amountRequested);

            sendMessage(player,ymlMsg.getPrefixMessage() + ymlMsg.getExpDepositTransactionMessage(amountRequested));
        } else {
            Experience.changePlayerExp(player, -exp);
            jsonManager.addExperience(playerUUID, exp);

            sendMessage(player,ymlMsg.getPrefixMessage() + ymlMsg.getExpDepositTransactionAdjustedMessage(exp));
        }
        bag.getExpDeposit().remove(player.getName());
    }

    public void withdrawalExp(Player player, UUID playerUUID, int amountRequested, BagInventory bag, JsonManager jsonManager, YmlMessage ymlMsg) throws IOException {
        int bagExp = jsonManager.getExperience(playerUUID);

        if(amountRequested == -1) amountRequested = bagExp;

        if(bagExp == 0) {
            sendMessage(player, ymlMsg.getExpWithdrawalTransactionErrorMessage(amountRequested));
            return;
        }

        if(bagExp >= amountRequested) {
            jsonManager.removeExperience(playerUUID, amountRequested);
            Experience.changePlayerExp(player, amountRequested);

            sendMessage(player,ymlMsg.getPrefixMessage() + ymlMsg.getExpWithdrawalTransactionMessage(amountRequested));
        } else {
            Experience.changePlayerExp(player, bagExp);
            jsonManager.removeExperience(playerUUID, bagExp);

            sendMessage(player, ymlMsg.getPrefixMessage() + ymlMsg.getExpWithdrawalTransactionAdjustedMessage(bagExp));
        }
        bag.getExpWithdrawal().remove(player.getName());
    }
}
