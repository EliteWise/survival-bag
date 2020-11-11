package fr.politicraft.bag.command;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.util.CustomItem;
import fr.politicraft.bag.util.InventoryUX;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Bag implements CommandExecutor {

    private Main main;
    private FileConfiguration config;
    private CustomItem customItem;
    private InventoryUX inventoryUX;

    public Bag(Main main) {
        this.main = main;
        this.config = main.getConfig();
        this.customItem = new CustomItem();
        this.inventoryUX = new InventoryUX();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Inventory bag = Bukkit.createInventory(null, 54, "Sac");

        config.getConfigurationSection("Bag").getKeys(false).forEach(category -> {

            inventoryUX.fillGlassPane(bag, 45);

            bag.setItem(4, customItem.create(Material.EXPERIENCE_BOTTLE, "§eXP"));

            bag.setItem(config.getInt("Bag." + category + ".Position"),
                    customItem.create(Material.valueOf(config.getString("Bag." + category + ".DisplayItem").toUpperCase()),
                                        config.getString("Bag." + category + ".DisplayName")));
        });
        player.openInventory(bag);
        return false;
    }
}
