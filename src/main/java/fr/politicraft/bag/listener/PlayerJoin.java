package fr.politicraft.bag.listener;

import fr.politicraft.bag.Main;
import fr.politicraft.bag.data.JsonManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.IOException;
import java.util.UUID;

public class PlayerJoin implements Listener {

    private Main main;

    public PlayerJoin(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws IOException {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        JsonManager jsonManager = new JsonManager(main, player);
        jsonManager.createPlayerFile(player);
    }
}
