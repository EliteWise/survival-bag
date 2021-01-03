package fr.politicraft.bag.config;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.util.JsonField;
import fr.politicraft.bag.util.YmlFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class YmlPermission {

    private Main main;
    private FileConfiguration config;

    public YmlPermission(Main main) {
        this.main = main;
        this.config = YamlConfiguration.loadConfiguration(new File(main.getDataFolder() + "/" + YmlFile.PERMISSIONS));
    }

    public boolean hasBagViewer(UUID playerUUID) {
        return config.getStringList(JsonField.BAG_VIEWER).contains(String.valueOf(UUIDModeEnabled() ? playerUUID : Bukkit.getOfflinePlayer(playerUUID).getName()));
    }

    public List<String> getBagViewers() {
        return config.getStringList(JsonField.BAG_VIEWER);
    }

    public boolean UUIDModeEnabled() {
        return config.getBoolean(JsonField.UUID_MODE);
    }

    public void fromUsernameToUUID() {

    }
}
