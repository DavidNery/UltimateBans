package me.dery.ultimatebans.commands;

import me.dery.ultimatebans.UltimateBans;
import me.dery.ultimatebans.managers.BanManager;
import me.dery.ultimatebans.objects.Ban;
import me.dery.ultimatebans.utils.OfflinePlayerUtils;
import me.dery.ultimatebans.utils.TimeUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class BanCommand extends Command {

    private final BanManager banManager;

    public BanCommand(UltimateBans instance) {
        super("ban");
        setAliases(Arrays.asList("tempban"));

        this.banManager = instance.getBanManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if (!sender.isOp()) return false;

        if (args.length == 0) {
            sender.sendMessage("§c§lUltimateBans §7Use §c/ban <player> [duration] [reason]");
            return false;
        }

        OfflinePlayer player = OfflinePlayerUtils.findOfflinePlayer(args[0]);
        if (player == null) {
            sender.sendMessage("§c§lUltimateBans §7This player has never joined the server");
            return false;
        }

        if (args.length == 1) {
            Ban ban = banManager.applyBan(player, -1, null, sender instanceof Player player1 ? player1.getName() : "CONSOLE");
            sender.sendMessage("§a§lUltimateBans §7You banned the player §a" + player.getName() + " §7permanently!");
            Player onlinePlayer = player.getPlayer();
            if (onlinePlayer != null)
                onlinePlayer.kickPlayer("§cYou have been permanently banned from the server by §f" + ban.getAppliedBy());
        } else {
            long time = TimeUtils.transformStringInLong(args[1]);
            String reason = null;
            if (args.length >= 3) {
                String[] reasonArray = new String[args.length - 2];
                System.arraycopy(args, 2, reasonArray, 0, reasonArray.length);
                reason = String.join(" ", reasonArray);
            }

            Ban ban = banManager.applyBan(player, System.currentTimeMillis() + time, reason, sender instanceof Player player1 ? player1.getName() : "CONSOLE");
            sender.sendMessage("§a§lUltimateBans §7You banned the player §a" + player.getName() + " §7for §a" + TimeUtils.format(time) + "§7!");

            Player onlinePlayer = player.getPlayer();
            if (onlinePlayer != null)
                onlinePlayer.kickPlayer("§cYou have been banned from the server by §f" + ban.getAppliedBy() + "§c." +
                        "\n\n§cReason: §f" + reason + "" +
                        "\n\n§cBanned until: §f" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ban.getUntil()))
                );
        }

        return true;
    }
}
