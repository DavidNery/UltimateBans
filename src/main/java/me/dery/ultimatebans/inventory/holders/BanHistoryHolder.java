package me.dery.ultimatebans.inventory.holders;

import me.dery.ultimatebans.objects.UltimateBanPlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class BanHistoryHolder implements InventoryHolder {

    private final UltimateBanPlayer player;
    private final int currentPage, totalPages;

    public BanHistoryHolder(UltimateBanPlayer player, int currentPage, int totalPages) {
        this.player = player;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
    }

    public UltimateBanPlayer getPlayer() {
        return player;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getBanIndexBySlot(int slot) {
        if (currentPage == 1) return slot;
        return (slot + 1) + (52 * (totalPages - 2)) + 53; // 53 because of the first page
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
