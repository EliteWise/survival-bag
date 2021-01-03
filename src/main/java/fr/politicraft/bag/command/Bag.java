package fr.politicraft.bag.command;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.core.InventoryManager;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.util.CustomItem;
import fr.politicraft.bag.util.InventoryUX;
import fr.politicraft.bag.util.YmlFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Bag implements CommandExecutor {

    private Main main;
    private FileConfiguration config;
    private CustomItem customItem;
    private InventoryUX inventoryUX;
    private JsonManager jsonManager;
    private InventoryManager inventoryManager;

    public Bag(Main main) {
        this.main = main;
        this.config = YamlConfiguration.loadConfiguration(new File(main.mainPath, YmlFile.CATEGORIES));
        this.customItem = new CustomItem();
        this.inventoryUX = new InventoryUX(main);
        this.jsonManager = new JsonManager(main);
        this.inventoryManager = new InventoryManager(main);
    }

    public void bagInventory(Player player) {
        Inventory bag = Bukkit.createInventory(null, 54, "Sac");

        config.getConfigurationSection("Bag").getKeys(false).forEach(category -> {

            if(main.getYmlBag().isFrameItemsEnabled()) inventoryUX.fillGlassPane(bag, 45);

            bag.setItem(main.getYmlBag().getExperienceItemSlot(), customItem.create(main.getYmlBag().getExperienceItemName(), main.getYmlBag().getExperienceItem(), main.getYmlBag().getExperienceItemDescription()));
            try {
                String symbol = (jsonManager.isHotbarEnabled(player.getUniqueId()) ? "§a✔" : "§c❌");
                bag.setItem(main.getYmlBag().getAutomaticSortItemSlot(), customItem.create(main.getYmlBag().getAutomaticSortItemName(), main.getYmlBag().getAutomaticSortItem(), main.getYmlBag().getAutomaticSortDescription(symbol)));
            } catch (IOException e) {
                e.printStackTrace();
            }

            bag.setItem(config.getInt("Bag." + category + ".Position"),
                    customItem.create(config.getString("Bag." + category + ".DisplayName"),
                            Material.valueOf(config.getString("Bag." + category + ".DisplayItem").toUpperCase()),
                            main.getYmlBag().getCategoriesDescription(0, null)));
        });
        player.openInventory(bag);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;

        try {
            jsonManager.createPlayerFile(player);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(args.length == 0) {
            bagInventory(player);
        } else if(args.length == 1) {
            switch (args[0]) {
                case "help":
                    String[] helpMsg = {"§e§m                       §r" + main.getYmlMsg().getPrefixMessage().replace(" ", "") + "§e§m                       \n" +
                                        "§e/sac \n" +
                                        "§e/sac trier \n" +
                                        "§e/sac recap \n" +
                                        "§e/sac blacklist \n" +
                                        "§e§m                                                     "};
                    player.sendMessage(helpMsg);
                    break;
                case "trier":
                    try {
                        inventoryManager.sort(player, jsonManager);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case "recap":
                    Inventory recapInventory = Bukkit.createInventory(null, 54, "Trie Récap");
                    recapInventory.setContents(main.getBagInventory().getSummarySortedItems().get(player.getName()));
                    try {
                        List<String> blacklistedItems = jsonManager.getBlacklistedItems(player.getUniqueId());
                        blacklistedItems.forEach(item -> recapInventory.remove(Material.valueOf(item)));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.openInventory(recapInventory);
                    break;
                case "blacklist":
                    Inventory blacklistInventory = Bukkit.createInventory(null, 54, "Blacklist");
                    int blInvSize = blacklistInventory.getSize();
                    try {
                        ItemStack[] blacklistedItems = jsonManager.getBlacklistedItems(player.getUniqueId()).stream().map(Material::valueOf).map(ItemStack::new).toArray(ItemStack[]::new);
                        ItemStack[] limitedBlacklistedItems = Arrays.stream(blacklistedItems).limit(blInvSize).toArray(ItemStack[]::new);
                        blacklistInventory.setContents(limitedBlacklistedItems);

                        if(blacklistedItems.length > blInvSize) {
                            ItemStack[] remainingItems = Arrays.stream(blacklistedItems).skip(blInvSize).toArray(ItemStack[]::new);
                            /*Inventory recapInventorySecondPage = Bukkit.createInventory(null, 54, "Trie Récap 2");
                            recapInventorySecondPage.setContents(remainingItems);*/
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    player.openInventory(blacklistInventory);
                    break;
                default:
                    String playerName = args[0];
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                    if(main.getYmlPerm().hasBagViewer(player.getUniqueId())) {
                        if(offlinePlayer.hasPlayedBefore()) {
                            main.getBagInventory().getCheckedPlayer().put(player.getUniqueId(), offlinePlayer.getUniqueId()); // Player who check | Player checked
                            bagInventory(player);
                        } else {
                            player.sendMessage(main.getYmlMsg().getPrefixMessage() + main.getYmlMsg().getUnknownPlayerMessage());
                        }
                    } else {
                        player.sendMessage("§cVous n'avez pas la permission.");
                    }
                    break;
            }

        } else if(args.length == 2) {
            if(args[0].equalsIgnoreCase("viewer") && player.isOp()) {
                if(args[1].equalsIgnoreCase("list")) {
                    for(String viewer : main.getYmlPerm().getBagViewers()) {
                        player.sendMessage(ChatColor.YELLOW + String.valueOf(main.getYmlPerm().UUIDModeEnabled() ? Bukkit.getOfflinePlayer(UUID.fromString(viewer)).getName() : Bukkit.getOfflinePlayer(viewer).getName()));
                    }
                }
            }
        } /*else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("viewer") && player.isOp()) {
                if(args[1].equalsIgnoreCase("add") && args[2] != null) {
                    UUID playerUUID = Bukkit.getOfflinePlayer(args[2]).getUniqueId();
                    main.getYmlPerm().addBagViewer(playerUUID);
                }
            }
        }*/
        return false;
    }
}
