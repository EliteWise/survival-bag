package fr.politicraft.bag.model;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;

public class BagInventory {

    private HashMap<Player, String> categoryInventoryName = new HashMap<>();
    private HashMap<Player, ItemStack> previousClickedItem = new HashMap<>();

    private ArrayList<String> deposit = new ArrayList<>();
    private ArrayList<String> withdrawal = new ArrayList<>();

    private ArrayList<String> expDeposit = new ArrayList<>();
    private ArrayList<String> expWithdrawal = new ArrayList<>();

    public BagInventory() {

    }

    public HashMap<Player, String> getCategoryInventoryName() {
        return categoryInventoryName;
    }

    public HashMap<Player, ItemStack> getPreviousClickedItem() {
        return previousClickedItem;
    }

    public ArrayList<String> getDeposit() {
        return deposit;
    }

    public ArrayList<String> getWithdrawal() {
        return withdrawal;
    }

    public ArrayList<String> getExpDeposit() {
        return expDeposit;
    }

    public ArrayList<String> getExpWithdrawal() {
        return expWithdrawal;
    }
}
