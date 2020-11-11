package fr.politicraft.bag.model;

import java.util.HashMap;
import java.util.List;

public class PlayerBag {

    private String playerName;
    private float experience;
    private HashMap<String, Integer> items;
    private List<String> blacklistedItems;

    public PlayerBag(String playerName, float experience, HashMap<String, Integer> items, List<String> blacklistedItems) {
        this.playerName = playerName;
        this.experience = experience;
        this.items = items;
        this.blacklistedItems = blacklistedItems;
    }

    public String getPlayerName() {
        return playerName;
    }

    public float getExperience() {
        return experience;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public List<String> getBlacklistedItems() {
        return blacklistedItems;
    }
}
