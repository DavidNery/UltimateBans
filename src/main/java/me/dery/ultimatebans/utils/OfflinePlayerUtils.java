package me.dery.ultimatebans.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerUtils {

    public static OfflinePlayer findOfflinePlayer(String name) {
        for (OfflinePlayer offlinePlayer : Bukkit.getServer().getOfflinePlayers())
            if (offlinePlayer.getName().equalsIgnoreCase(name)) return offlinePlayer;

        return null;
    }

}
