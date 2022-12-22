package me.dery.ultimatebans.commands;

import me.dery.ultimatebans.UltimateBans;
import me.dery.ultimatebans.managers.BanManager;
import me.dery.ultimatebans.menus.BanHistoryMenu;
import me.dery.ultimatebans.objects.UltimateBanPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class HistoryCommand extends Command {

    private final BanManager banManager;

    public HistoryCommand(UltimateBans instance) {
        super("history");

        this.banManager = instance.getBanManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.isOp() || !(sender instanceof Player player)) return false;

        if (args.length == 0) {
            sender.sendMessage("§c§lUltimateBans §7Use §c/history <player>");
            return false;
        }

        Optional<UltimateBanPlayer> ultimateBanPlayer = banManager.findByName(args[0]);
        if (!ultimateBanPlayer.isPresent()) {
            sender.sendMessage("§c§lUltimateBans §7This player was never banned");
            return false;
        }

        player.openInventory(BanHistoryMenu.buildBanHistoryInventory(ultimateBanPlayer.get(), 1));

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (args.length == 1) {
            String arg = args[0].toLowerCase();
            return new ArrayList<>(banManager.getBannedPlayers())
                    .stream()
                    .filter(s -> s.toLowerCase().contains(arg))
                    .collect(Collectors.toList());
        }

        return Collections.emptyList();
    }
}
