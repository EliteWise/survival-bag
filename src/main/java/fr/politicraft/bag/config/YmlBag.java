package fr.politicraft.bag.config;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.util.TextAdapter;
import fr.politicraft.bag.util.YmlFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class YmlBag {

    private Main main;
    private FileConfiguration config;

    public YmlBag(Main main) {
        this.main = main;
        this.config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/" + YmlFile.BAG));
    }

    // Inventory //

    public String getInventoryMenuName() {
        return TextAdapter.colorize(config.getString("inventory-menu-name"));
    }

    public String getInventoryDepositWithdrawalName() {
        return TextAdapter.colorize(config.getString("inventory-deposit-withdrawal-name"));
    }

    public String getInventoryExperienceName() {
        return TextAdapter.colorize(config.getString("inventory-experience-name"));
    }

    public String getBackButtonName() {
        return TextAdapter.colorize(config.getString("back-button-name"));
    }

    public Material getBackButtonItem() {
        return Material.valueOf(config.getString("back-button-item"));
    }

    public int getBackButtonSlot() {
        return config.getInt("back-button-slot");
    }

    public boolean isFrameItemsEnabled() {
        return config.getBoolean("enable-frame-items");
    }

    public Material getFrameItem() {
        return Material.valueOf(config.getString("frame-item"));
    }

    public Material getExperienceItem() {
        return Material.valueOf(config.getString("experience-item"));
    }

    public String getExperienceItemName() {
        return TextAdapter.colorize(config.getString("experience-item-name"));
    }

    public List<String> getExperienceItemDescription() {
        return config.getStringList("experience-item-description").stream().map(TextAdapter::colorize).collect(Collectors.toList());
    }

    public int getExperienceItemSlot() {
        return config.getInt("experience-item-slot");
    }

    public Material getAutomaticSortItem() {
        return Material.valueOf(config.getString("automatic-sort-item"));
    }

    public String getAutomaticSortItemName() {
        return TextAdapter.colorize(config.getString("automatic-sort-item-name"));
    }

    public List<String> getAutomaticSortDescription(String symbol) {
        return config.getStringList("automatic-sort-item-description").stream().map(elem -> TextAdapter.replaceSection(elem, 0, symbol)).map(TextAdapter::colorize).collect(Collectors.toList());
    }

    public int getAutomaticSortItemSlot() {
        return config.getInt("automatic-sort-item-slot");
    }

    public List<String> getCategoriesDescription(int amount, String symbol) {
        return config.getStringList("categories-description").stream().map(elem -> TextAdapter.replaceSection(elem, amount, symbol)).map(TextAdapter::colorize).collect(Collectors.toList());
    }

    public List<String> getItemsDescription(int amount, String symbol) {
        return config.getStringList("items-description").stream().map(elem -> TextAdapter.replaceSection(elem, amount, symbol)).map(TextAdapter::colorize).collect(Collectors.toList());
    }

    public Material getDepositItem() {
        return Material.valueOf(config.getString("deposit-item"));
    }

    public String getDepositItemName() {
        return TextAdapter.colorize(config.getString("deposit-item-name"));
    }

    public Material getWithdrawalItem() {
        return Material.valueOf(config.getString("withdrawal-item"));
    }

    public String getWithdrawalItemName() {
        return TextAdapter.colorize(config.getString("withdrawal-item-name"));
    }

    public List<String> getDepositWithdrawalItemCounterDescription(int amount) {
        return config.getStringList("deposit-withdrawal-item-counter-description").stream().map(elem -> TextAdapter.replaceSection(elem, amount, null)).map(TextAdapter::colorize).collect(Collectors.toList());
    }

    public int getDepositWithdrawalItemCounterSlot() {
        return config.getInt("deposit-withdrawal-item-counter-slot");
    }

    public int getDepositItemSlot() {
        return config.getInt("deposit-item-slot");
    }

    public int getWithdrawalItemSlot() {
        return config.getInt("withdrawal-item-slot");
    }
}
