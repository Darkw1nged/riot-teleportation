package me.darkwinged.RiotTeleportation.libaries;

import me.darkwinged.RiotTeleportation.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.*;

public class Utils {

    private static final Main plugin = Main.getInstance;

    // ---- [ Managing chat color within the plugin ] ----
    public static String chatColor(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    // ---- [ Managing chat color within the plugin | Supports Amount ] ----
    public static String chatColor(String s, Double amount) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "US"));
        String converted = nf.format(amount);
        return ChatColor.translateAlternateColorCodes('&', s)
                .replaceAll("%amount%", converted);
    }

    // ---- [ Converting a lore to include colors ] ----
    public static List<String> getConvertedLore(FileConfiguration config, String path) {
        if (config == null) return null;
        List<String> oldList = config.getStringList(path + ".lore");
        List<String> newList = new ArrayList<>();
        for (String a : oldList)
            newList.add(ChatColor.translateAlternateColorCodes('&', a));
        return newList;
    }

    // ---- [ Available space ] ----
    public static boolean hasSpace(Player player, ItemStack targetItem) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() == targetItem.getType()) {
                if (item.getAmount() != item.getMaxStackSize()) {
                    item.setAmount(item.getAmount() + 1);
                    return true;
                }
            }
        }
        if (player.getInventory().firstEmpty() != -1) {
            player.getInventory().addItem(targetItem);
            return true;
        }
        return false;
    }

    // ---- [ Removes all color codes from string ] ----
    public static String replaceAllColorCodes(String string) {
        return string.replaceAll("&a", "")
                .replaceAll("&b", "")
                .replaceAll("&c", "")
                .replaceAll("&d", "")
                .replaceAll("&e", "")
                .replaceAll("&f", "")
                .replaceAll("&1", "")
                .replaceAll("&2", "")
                .replaceAll("&3", "")
                .replaceAll("&4", "")
                .replaceAll("&5", "")
                .replaceAll("&6", "")
                .replaceAll("&7", "")
                .replaceAll("&8", "")
                .replaceAll("&9", "")
                .replaceAll("&k", "")
                .replaceAll("&l", "")
                .replaceAll("&m", "")
                .replaceAll("&n", "")
                .replaceAll("&o", "")
                .replaceAll("&r", "");

    }

    // ---- [ Time formatting ] ----
    public static String formatTime(String time) {
        return time
                .replaceAll("d", "")
                .replaceAll("h", "")
                .replaceAll("m", "")
                .replaceAll("s", "");
    }

    public static int getRawTime(String path) {
        if (path.contains("d")) {
            return Integer.parseInt(Utils.formatTime(path)) * 24 * 60 * 60;
        } else if (path.contains("h")) {
            return Integer.parseInt(Utils.formatTime(path)) * 60 * 60;
        } else if (path.contains("m")) {
            return Integer.parseInt(Utils.formatTime(path)) * 60;
        } else {
            return Integer.parseInt(Utils.formatTime(path));
        }
    }

    // ---- [ Cached Items ] ----
    public static Map<UUID, UUID> teleport_ask = new HashMap<>();
    public static Map<UUID, Integer> teleportTimeout = new HashMap<>();
    public static HashMap<UUID, Integer> teleportAskDelay = new HashMap<>();
    public static HashMap<UUID, Integer> randomTeleportDelay = new HashMap<>();
}
