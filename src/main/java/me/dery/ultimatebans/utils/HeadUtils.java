package me.dery.ultimatebans.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class HeadUtils {

    public static ItemStack createHead(String owner, String name) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(owner);
        meta.setDisplayName(name);
        skull.setItemMeta(meta);
        return skull;
    }

    public static ItemStack createHeadWithTexture(String texture, String name, List<String> lore) {
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        try {
            Field profile = ReflectionUtils.getField(meta.getClass(), "profile");
            GameProfile gameProfile = (GameProfile) profile.get(meta);

            if (gameProfile == null)
                gameProfile = new GameProfile(UUID.randomUUID(), null);

            gameProfile.getProperties().put("textures", new Property("textures", texture));
            profile.set(meta, gameProfile);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        meta.setDisplayName(name);
        meta.setLore(lore);
        skull.setItemMeta(meta);
        return skull;
    }

}
