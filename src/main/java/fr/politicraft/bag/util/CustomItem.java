package fr.politicraft.bag.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class CustomItem {

    public ItemStack create(String itemName, Material material, List<String> desc) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(itemName);
        if(desc.stream().allMatch(elem -> elem != null && elem.length() > 0)) meta.setLore(desc);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack create(Material material, List<String> desc) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        if(desc.stream().allMatch(elem -> elem != null && elem.length() > 0)) meta.setLore(desc);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack create(Material material, String itemName) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(itemName);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public ItemStack create(Material material, int amount) {
        ItemStack itemStack = new ItemStack(material);
        ItemMeta meta = itemStack.getItemMeta();
        ArrayList<String> lores = new ArrayList<>();
        lores.add(String.valueOf(amount));
        meta.setLore(lores);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public String capitalize(String input, String split) {
        String output = "";
        for (String s : input.split(split)) {
            output += s.substring(0, 1).toUpperCase()+s.substring(1).toLowerCase()+" ";
        }
        return output.substring(0, output.length()-1);
    }
}
