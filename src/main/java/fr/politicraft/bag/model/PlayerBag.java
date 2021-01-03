package fr.politicraft.bag.model;

import java.util.HashMap;
import java.util.List;

public class PlayerBag {

    private String playerName;
    private int experience;
    private HashMap<String, Integer> items;
    private List<String> blacklistedItems;
    private HashMap<String, Object> config;

    public PlayerBag(String playerName, int experience, HashMap<String, Integer> items, List<String> blacklistedItems, HashMap<String, Object> config) {
        this.playerName = playerName;
        this.experience = experience;
        this.items = items;
        this.blacklistedItems = blacklistedItems;
        this.config = config;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getExperience() {
        return experience;
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public List<String> getBlacklistedItems() {
        return blacklistedItems;
    }

    public HashMap<String, Object> getConfig() {
        return config;
    }
}
