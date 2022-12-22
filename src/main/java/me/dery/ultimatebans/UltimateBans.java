package me.dery.ultimatebans;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.dery.ultimatebans.commands.BanCommand;
import me.dery.ultimatebans.commands.HistoryCommand;
import me.dery.ultimatebans.listeners.InventoryListeners;
import me.dery.ultimatebans.listeners.PlayerListeners;
import me.dery.ultimatebans.managers.BanManager;
import me.dery.ultimatebans.utils.ReflectionUtils;
import org.bukkit.command.CommandMap;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class UltimateBans extends JavaPlugin {

    private BanManager banManager;

    @Override
    public void onEnable() {
        final ConsoleCommandSender sender = getServer().getConsoleSender();
        sendPrefixedMessage(sender, "Loading plugin...");

        sendPrefixedMessage(sender, "Trying to setup Storage...");
        try {
            HikariConfig hikariConfig = new HikariConfig();
            hikariConfig.setJdbcUrl("jdbc:mysql://localhost:3306/server");
            hikariConfig.setUsername("root");
            hikariConfig.setPassword("");
            hikariConfig.setMaximumPoolSize(3);
            hikariConfig.setMaxLifetime(TimeUnit.MINUTES.toMillis(25L));

            banManager = new BanManager(new HikariDataSource(hikariConfig));
        } catch (Exception e) {
            sendPrefixedMessage(sender, "Failed to setup storage due: " + e.getCause().getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        sendPrefixedMessage(sender, "Storage setup successfully");

        sendPrefixedMessage(sender, "Registering listeners...");
        registerListeners();
        sendPrefixedMessage(sender, "Listeners registered!");

        sendPrefixedMessage(sender, "Registering commands...");
        registerCommands();
        sendPrefixedMessage(sender, "Commands registered!");

        sendPrefixedMessage(sender, "Plugin loaded!");
    }

    @Override
    public void onDisable() {
        final ConsoleCommandSender sender = getServer().getConsoleSender();
        sendPrefixedMessage(sender, "Unloading plugin...");
        banManager.saveAll();
        sendPrefixedMessage(sender, "Plugin unloaded!");
    }

    public BanManager getBanManager() {
        return banManager;
    }

    private void registerListeners() {
        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new InventoryListeners(this), this);
        manager.registerEvents(new PlayerListeners(this), this);
    }

    private void registerCommands() {
        CommandMap commandMap = ReflectionUtils.getMethodInvocation(
                ReflectionUtils.getMethod(getServer().getClass(), "getCommandMap"),
                getServer()
        );

        final String plugin = getDescription().getName().toLowerCase();
        commandMap.registerAll(plugin, Arrays.asList(
                new BanCommand(this), new HistoryCommand(this)
        ));
    }

    private void sendPrefixedMessage(ConsoleCommandSender sender, String msg) {
        sender.sendMessage(String.format("[%s] %s", getDescription().getName(), msg));
    }
}
