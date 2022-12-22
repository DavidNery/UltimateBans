package me.dery.ultimatebans.objects;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class UltimateBanPlayer {

    private final UUID uuid;
    private String name;
    private List<Ban> banHistory;

    public UltimateBanPlayer(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.banHistory = new LinkedList<>();
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ban> getBanHistory() {
        return banHistory;
    }
}
