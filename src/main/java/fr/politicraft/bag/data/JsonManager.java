package fr.politicraft.bag.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import fr.politicraft.bag.Main;
import fr.politicraft.bag.model.PlayerBag;
import fr.politicraft.bag.util.JsonField;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class JsonManager {

    // create object mapper instance
    private static final ObjectMapper mapper = new ObjectMapper();

    private Main main;
    private String mainPath;

    public JsonManager(Main main) {
        this.main = main;
        this.mainPath = main.getDataFolder().getPath() + "/players/";
    }

    public JsonManager(Main main, Player player) throws IOException {
        this.main = main;
        this.mainPath = main.getDataFolder().getPath() + "/players/";
        createPlayerFile(player);
    }

    public boolean itemExist(UUID playerUUID, String item) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        // Check value
        ObjectNode itemNode = (ObjectNode) root.get("items");
        if(itemNode != null) {
            return true;
        }
        return false;
    }

    public int getItemAmount(UUID playerUUID, String itemName) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        if(itemExist(playerUUID, itemName)) {
            JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
            JsonNode item = root.get("items").findValue(itemName);

            if(item != null) {
                return item.intValue();
            }
        }
        return 0;
    }

    public void addAllItems(UUID playerUUID, ItemStack[] items) throws IOException {
        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));

        // Update item
        ObjectNode itemNode = (ObjectNode) root.get("items");
        List<String> blacklistedItems = getBlacklistedItems(playerUUID);
        for(ItemStack item : items) {
            if(item != null && !blacklistedItems.contains(item.getType().name())) {
                itemNode.put(String.valueOf(item.getType()), itemNode.path(String.valueOf(item.getType())).intValue() + item.getAmount());
                Bukkit.getPlayer(playerUUID).getInventory().removeItem(item);
            }
        }

        // Write new value into json file
        mapper.writeValue(new File(mainPath + playerUUID + ".json"), root);
    }

    public void addItemAmount(UUID playerUUID, String itemName, int itemAmount) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        if (itemExist(playerUUID, itemName)) {
            // Update item
            ObjectNode itemNode = (ObjectNode) root.get("items");
            itemNode.put(itemName, itemNode.path(itemName).intValue() + itemAmount);
        } else {
            // Create item
            ObjectNode itemNode = (ObjectNode) root.path("items");
            itemNode.put(itemName, itemAmount);
        }
        // Write new value into json file
        mapper.writeValue(new File(mainPath + playerUUID + ".json"), root);
    }

    public void removeItemAmount(UUID playerUUID, String itemName, int itemAmount) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        // Update item
        ObjectNode itemNode = (ObjectNode) root.get("items");
        itemNode.put(itemName, itemNode.path(itemName).intValue() - itemAmount);
        if(itemNode.path(itemName).intValue() >= 0) {
            mapper.writeValue(new File(mainPath + playerUUID + ".json"), root);
        }
    }

    public void createPlayersFolder() {
        boolean success = new File(mainPath).mkdirs();

        if(!success) {
            System.out.println("Folder 'players' already initialized!");
        }
    }

    public void createPlayerFile(Player player) throws IOException {
        UUID playerUUID = player.getUniqueId();
        File file = new File(mainPath, playerUUID + ".json");

        if(!file.exists()) {
            FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
            fileConfiguration.save(file);

            HashMap<String, Object> baseConfig = new HashMap<String, Object>() {{
                put("sort", "all");
            }};

            // Our Model
            PlayerBag playerBag = new PlayerBag(player.getName(), 0, new HashMap<>(), new ArrayList<>(), baseConfig);
            mapper.writeValue(new File(mainPath + playerUUID + ".json"), playerBag);
        }
    }

    public int getExperience(UUID playerUUID) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        JsonNode item = root.get("experience");

        if(item != null) {
            return item.intValue();
        }
        return 0;
    }

    public void addExperience(UUID playerUUID, float exp) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        ((ObjectNode) root).put("experience", root.get("experience").intValue() + exp);
        mapper.writeValue(new File(mainPath + playerUUID + ".json"), root);
    }

    public void removeExperience(UUID playerUUID, int exp) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        ((ObjectNode) root).put("experience", root.get("experience").intValue() - exp);
        mapper.writeValue(new File(mainPath + playerUUID + ".json"), root);
    }

    public boolean isBlacklisted(UUID playerUUID, String item) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        // Check value
        if(root.get("items").get(item) != null) {
            return true;
        }
        return false;
    }

    public boolean blacklistItemExist(UUID playerUUID, String itemName) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        // Check value
        ArrayNode arrayNode = (ArrayNode) root.get(JsonField.BLACKLIST);
        for(int i = arrayNode.size() - 1; i >= 0; i--) {
            if(arrayNode.get(i).asText().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getBlacklistedItems(UUID playerUUID) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        // Check value
        ArrayNode arrayNode = (ArrayNode) root.get(JsonField.BLACKLIST);
        List<String> blacklistedItems = new ArrayList<>();
        for(int i = arrayNode.size() - 1; i >= 0; i--) {
            blacklistedItems.add(arrayNode.get(i).asText());
        }
        return blacklistedItems;
    }

    public void updateBlacklistedItem(UUID playerUUID, String itemName) throws IOException {
        UUID playerChecked = main.getBagInventory().getCheckedPlayer().get(playerUUID);
        playerUUID = (playerChecked == null ? playerUUID : playerChecked);

        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        if(!blacklistItemExist(playerUUID, itemName)) {
            ArrayNode arrayNode = (ArrayNode) root.get(JsonField.BLACKLIST);
            arrayNode.add(itemName);
            ((ObjectNode) root).put(JsonField.BLACKLIST, arrayNode);
        } else {
            ArrayNode arrayNode = (ArrayNode) root.get(JsonField.BLACKLIST);
            for(int i = arrayNode.size() - 1; i >= 0; i--) {
                if(arrayNode.get(i).asText().equalsIgnoreCase(itemName)) {
                    arrayNode.remove(i);
                }
            }
            ((ObjectNode) root).put(JsonField.BLACKLIST, arrayNode);
        }
        mapper.writeValue(new File(mainPath + playerUUID + ".json"), root);
    }

    public String getSortConfig(UUID playerUUID) throws IOException {
        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        return root.get(JsonField.CONFIG).get(JsonField.CONFIG_SORT).textValue();
    }

    private String[] modes = new String[] {"all", "hotbar-only", "all-except-hotbar"};

    public void updateSortConfig(UUID playerUUID) throws IOException {
        JsonNode root = mapper.readTree(new File(mainPath + playerUUID + ".json"));
        int indexMode = ArrayUtils.indexOf(modes, getSortConfig(playerUUID));
        int limit = 2;
        int nexIndexMode = (indexMode == limit ? indexMode - limit : indexMode + 1);

        ((ObjectNode) root.get(JsonField.CONFIG)).put(JsonField.CONFIG_SORT, modes[nexIndexMode]);
        mapper.writeValue(new File(mainPath + playerUUID + ".json"), root);
    }

}
