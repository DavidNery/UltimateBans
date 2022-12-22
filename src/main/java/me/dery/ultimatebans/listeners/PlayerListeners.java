package me.dery.ultimatebans.listeners;

import me.dery.ultimatebans.UltimateBans;
import me.dery.ultimatebans.managers.BanManager;
import me.dery.ultimatebans.objects.Ban;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

public class PlayerListeners implements Listener {

    private BanManager banManager;

    public PlayerListeners(UltimateBans instance) {
        this.banManager = instance.getBanManager();
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        banManager.find(e.getUniqueId()).ifPresent(player -> {
            player.setName(e.getName());

            long now = System.currentTimeMillis();
            for (Ban ban : player.getBanHistory()) {
                if (ban.isActive() || ban.isPermanent()) {
                    if (now < ban.getUntil()) {
                        e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
                        if (ban.getReason() != null) e.setKickMessage(ban.getReason());
                        break;
                    } else {
                        ban.setActive(false);
                    }
                }
            }
        });
    }
}
