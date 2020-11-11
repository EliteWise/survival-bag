package fr.politicraft.bag.listener;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.core.InventoryManager;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.model.BagInventory;
import fr.politicraft.bag.util.CustomItem;
import fr.politicraft.bag.util.Experience;
import net.minecraft.server.v1_16_R2.LocaleLanguage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R2.inventory.CraftItemStack;
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

    public ChatAmount(Main main) {
        this.main = main;
        this.inventoryManager = new InventoryManager(main);
        this.customItem = new CustomItem();
    }

    public static String getItemName(ItemStack item) {
        net.minecraft.server.v1_16_R2.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
        return LocaleLanguage.a().a(nmsStack.getItem().getName());
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
        UUID playerUUID = chatPlayer.getUniqueId();
        String playerName = chatPlayer.getName();
        Inventory playerInv = chatPlayer.getInventory();
        String msg = e.getMessage();
        BagInventory bag = main.getBagInventory();
        JsonManager jsonManager = new JsonManager(main);

        Material previousClickedItem = bag.getPreviousClickedItem().get(chatPlayer).getType();

        if(bag.getDeposit().contains(playerName)) {
            if(playerInv.contains(previousClickedItem, Integer.parseInt(msg))) {
                jsonManager.addItemAmount(chatPlayer.getUniqueId(), previousClickedItem.toString(), Integer.parseInt(msg));
                playerInv.removeItem(new ItemStack(previousClickedItem, Integer.parseInt(msg)));
                chatPlayer.sendMessage("§7[§eSac§7] §e" + Integer.parseInt(msg) + " §7" + customItem.capitalize(previousClickedItem.getKey().getKey(), "_") + " §aont été déposé dans votre sac.");
            } else {
                chatPlayer.sendMessage("§7[§eSac§7] §cVous ne possédez pas §e" + Integer.parseInt(msg) + " §7" + customItem.capitalize(previousClickedItem.getKey().getKey(), "_") + "§c dans votre inventaire.");
            }
            e.setCancelled(true);
            bag.getDeposit().remove(chatPlayer.getName());
            reopenAmountInventory(chatPlayer, jsonManager, previousClickedItem, false);
        } else if(bag.getWithdrawal().contains(playerName)) {
            if(jsonManager.getItemAmount(playerUUID, previousClickedItem.toString()) >= Integer.parseInt(msg)) {
                jsonManager.removeItemAmount(chatPlayer.getUniqueId(), previousClickedItem.toString(), Integer.parseInt(msg));
                playerInv.addItem(new ItemStack(previousClickedItem, Integer.parseInt(msg)));
                bag.getWithdrawal().remove(chatPlayer.getName());
                chatPlayer.sendMessage("§7[§eSac§7] §e" + Integer.parseInt(msg) + " §7" + customItem.capitalize(previousClickedItem.getKey().getKey(), "_") + " §aont été retiré de votre sac.");
            } else {
                chatPlayer.sendMessage("§7[§eSac§7] §cVous ne possédez pas §e" + Integer.parseInt(msg) + " §7" + customItem.capitalize(previousClickedItem.getKey().getKey(), "_") + "§c dans votre sac.");
            }
            e.setCancelled(true);
            reopenAmountInventory(chatPlayer, jsonManager, previousClickedItem, false);
        } else if(bag.getExpDeposit().contains(playerName)) {
            if(Experience.getPlayerExp(chatPlayer) >= Integer.parseInt(msg)) {
                jsonManager.addExperience(chatPlayer.getUniqueId(), Float.parseFloat(msg));
                Experience.changePlayerExp(chatPlayer, - Integer.parseInt(msg));
                chatPlayer.sendMessage("§7[§eSac§7] §e" + Integer.parseInt(msg) + " exp §aont été déposé dans votre sac.");
            } else {
                chatPlayer.sendMessage("§7[§eSac§7] §cVous ne possédez pas §e" + Integer.parseInt(msg) + " exp.");
            }
            e.setCancelled(true);
            bag.getExpDeposit().remove(chatPlayer.getName());
            reopenAmountInventory(chatPlayer, jsonManager, previousClickedItem, true);
        } else if(bag.getExpWithdrawal().contains(playerName)) {
            if(jsonManager.getExperience(playerUUID) >= Float.parseFloat(msg)) {
                jsonManager.removeExperience(chatPlayer.getUniqueId(), Float.parseFloat(msg));
                Experience.changePlayerExp(chatPlayer, Integer.parseInt(msg));
                bag.getExpWithdrawal().remove(chatPlayer.getName());
                chatPlayer.sendMessage("§7[§eSac§7] §e" + Integer.parseInt(msg) + " exp §aont été retiré de votre sac.");
            } else {
                chatPlayer.sendMessage("§7[§eSac§7] §cVous ne possédez pas §e" + Integer.parseInt(msg) + " exp §cdans votre sac.");
            }
            e.setCancelled(true);
            reopenAmountInventory(chatPlayer, jsonManager, previousClickedItem, true);
        }
    }
}
