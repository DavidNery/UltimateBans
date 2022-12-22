package me.dery.ultimatebans.listeners;

import me.dery.ultimatebans.UltimateBans;
import me.dery.ultimatebans.inventory.holders.BanHistoryHolder;
import me.dery.ultimatebans.managers.BanManager;
import me.dery.ultimatebans.menus.BanHistoryMenu;
import me.dery.ultimatebans.objects.Ban;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryListeners implements Listener {

    private BanManager banManager;

    public InventoryListeners(UltimateBans instance) {
        this.banManager = instance.getBanManager();
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof BanHistoryHolder holder && e.getCurrentItem() != null) {
            ItemStack item = e.getCurrentItem();
            if (item.getType() == Material.AIR) return;

            e.setCancelled(true);
            if (e.getSlot() == 45 && holder.getCurrentPage() > 1) { // back
                e.getWhoClicked().getOpenInventory().close();
                e.getWhoClicked().openInventory(BanHistoryMenu.buildBanHistoryInventory(holder.getPlayer(), holder.getCurrentPage() - 1));
            } else if (e.getSlot() == 53 && holder.getCurrentPage() < holder.getTotalPages()) { // back
                e.getWhoClicked().getOpenInventory().close();
                e.getWhoClicked().openInventory(BanHistoryMenu.buildBanHistoryInventory(holder.getPlayer(), holder.getCurrentPage() + 1));
            }

            if (e.getClick() == ClickType.RIGHT) {
                Ban ban = holder.getPlayer().getBanHistory().get(holder.getBanIndexBySlot(e.getSlot()));
                if (!ban.isActive()) return;

                banManager.unban(ban, (Player) e.getWhoClicked());
                e.getWhoClicked().sendMessage("§eUltimateBans §7You have successfully removed this ban from this user!");
                ((Player) e.getWhoClicked()).playSound(e.getWhoClicked().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
            }
        }
    }
}
