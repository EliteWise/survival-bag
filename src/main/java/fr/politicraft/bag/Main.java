package fr.politicraft.bag;

import fr.politicraft.bag.command.Bag;
import fr.politicraft.bag.data.JsonManager;
import fr.politicraft.bag.listener.ChatAmount;
import fr.politicraft.bag.listener.InventoryClick;
import fr.politicraft.bag.listener.PlayerJoin;
import fr.politicraft.bag.model.BagInventory;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {

    private BagInventory bagInventory = new BagInventory();

    @Override
    public void onEnable() {

        saveDefaultConfig();

        PluginManager pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new ChatAmount(this), this);
        pluginManager.registerEvents(new InventoryClick(this), this);
        pluginManager.registerEvents(new PlayerJoin(this), this);

        getCommand("sac").setExecutor(new Bag(this));

        JsonManager jsonManager = new JsonManager(this);
        jsonManager.createPlayersFolder();
    }

    @Override
    public void onDisable() {

    }

    public BagInventory getBagInventory() {
        return this.bagInventory;
    }

}
