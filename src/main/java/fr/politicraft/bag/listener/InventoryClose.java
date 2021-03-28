package fr.politicraft.bag.listener;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.data.JsonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;
import java.util.UUID;

public class InventoryClose implements Listener {

    private Main main;
    private JsonManager jsonManager;

    public InventoryClose(Main main) {
        this.main = main;
        this.jsonManager = new JsonManager(main);
    }

    @EventHandler
    public void onPlayerCloseSpecialInventory(InventoryCloseEvent e) throws IOException {
        Player player = (Player) e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        String title = e.getView().getTitle();

        if(title.equalsIgnoreCase(main.getYmlBag().getSpecialInventoryName())) {
            Inventory specialInv = e.getInventory();
            jsonManager.saveSpecialInventory(playerUUID, specialInv);
        }
    }
}
