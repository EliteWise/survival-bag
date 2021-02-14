package fr.politicraft.bag;

import fr.politicraft.bag.command.Bag;
import fr.politicraft.bag.config.YmlBag;
import fr.politicraft.bag.config.YmlMessage;
import fr.politicraft.bag.config.YmlPermission;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.listener.ChatAmount;
import fr.politicraft.bag.listener.InventoryClick;
import fr.politicraft.bag.listener.PlayerJoin;
import fr.politicraft.bag.model.BagInventory;
import fr.politicraft.bag.util.YmlFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;

public final class Main extends JavaPlugin {

    private BagInventory bagInventory = new BagInventory();
    private YmlMessage ymlMessage;
    private YmlPermission ymlPerm;
    private YmlBag ymlBag;

    public String mainPath = getDataFolder() + "/";
    public List<String> ressources = Arrays.asList(YmlFile.BAG, YmlFile.CATEGORIES, YmlFile.MESSAGES, YmlFile.PERMISSIONS);

    @Override
    public void onEnable() {

        JsonManager jsonManager = new JsonManager(this);
        jsonManager.createPlayersFolder();

        ressources.forEach(r -> {
            saveResource(r, false);
        });

        setupConfig();

        getCommand("sac").setExecutor(new Bag(this));

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ChatAmount(this), this);
        pluginManager.registerEvents(new InventoryClick(this), this);
        pluginManager.registerEvents(new PlayerJoin(this), this);
    }

    @Override
    public void onDisable() {
    }

    public void setupConfig() {
        ymlMessage = new YmlMessage(this);
        ymlPerm = new YmlPermission(this);
        ymlBag = new YmlBag(this);
    }

    public BagInventory getBagInventory() {
        return this.bagInventory;
    }

    public YmlMessage getYmlMsg() { return ymlMessage; }

    public YmlPermission getYmlPerm() { return ymlPerm; }

    public YmlBag getYmlBag() { return ymlBag; }

}
