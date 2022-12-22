package me.dery.ultimatebans.menus;

import me.dery.ultimatebans.inventory.holders.BanHistoryHolder;
import me.dery.ultimatebans.objects.Ban;
import me.dery.ultimatebans.objects.UltimateBanPlayer;
import me.dery.ultimatebans.utils.HeadUtils;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BanHistoryMenu {

    public static Inventory buildBanHistoryInventory(UltimateBanPlayer player, int page) {
        int totalPages = (int) Math.ceil(player.getBanHistory().size() / 52f);
        Inventory inventory = Bukkit.getServer().createInventory(
                new BanHistoryHolder(player, page, totalPages),
                54, "§7Bans of player §f" + player.getName() + " §7- §f" + page + "§7/§f" + totalPages
        );

        inventory.setItem(45, HeadUtils.createHead("MHF_ArrowLeft", page == 1 ? ("§cNo previous page") : ("§eBack to page §f" + (page - 1))));
        inventory.setItem(53, HeadUtils.createHead("MHF_ArrowRight", page == totalPages ? ("§cNo next page") : ("§eGo to page §f" + (page + 1))));

        int index = (inventory.getSize() - 2) * (page - 1), max = Math.min(index + inventory.getSize() - 2, player.getBanHistory().size());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long now = System.currentTimeMillis();
        for (int i = index; i < max; i++) {
            Ban ban = player.getBanHistory().get(i);
            if (ban.isActive() && now > ban.getUntil()) ban.setActive(false);

            List<String> lore = new ArrayList<>(Arrays.asList(
                    " §7Player banned by: §f" + ban.getAppliedBy(),
                    " §7Player banned because: §f" + ban.getReason(),
                    " §7Player banned in: §f" + sdf.format(new Date(ban.getWhen()))
            ));

            if (ban.isActive()) {
                lore.add(" §7Player banned until: §f" + (ban.isPermanent() ? "permanently" : sdf.format(new Date(ban.getUntil()))));
                lore.add("");
                lore.add(" §eRight §7click to remove this ban from the player");
            } else {
                if (ban.getUnbannedBy() != null)
                    lore.add(" §7Ban removed by staff §f" + ban.getUnbannedBy() + " §7in §f" + sdf.format(new Date(ban.getUntil())));
                else
                    lore.add(" §7Ban expired!");
            }

            ItemStack head = HeadUtils.createHeadWithTexture(
                    "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvM2VkMWFiYTczZjYzOWY0YmM0MmJkNDgxOTZjNzE1MTk3YmUyNzEyYzNiOTYyYzk3ZWJmOWU5ZWQ4ZWZhMDI1In19fQ==",
                    "§eBan",
                    lore
            );

            inventory.addItem(head);
        }

        return inventory;
    }

}
