package fr.politicraft.bag.util;

import org.bukkit.ChatColor;

public class TextAdapter {

    public static String colorize(String text)
    {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static String replaceSection(String text, int amount, String symbol) {
        String amountSection = "{amount}";
        String itemSection = "{item}";
        String symbolSection = "{symbol}";
        text = text.contains(amountSection) ? text.replace(amountSection, String.valueOf(amount)) : text;
        text = text.contains(itemSection) ? text.replace(itemSection, "<item>") : text;
        text = text.contains(symbolSection) ? text.replace(symbolSection, symbol) : text;
        return text;
    }

}
