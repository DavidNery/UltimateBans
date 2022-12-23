package me.dery.ultimatebans.managers;

import me.dery.ultimatebans.objects.Ban;
import me.dery.ultimatebans.objects.UltimateBanPlayer;
import me.dery.ultimatebans.taks.ConcurrentTask;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.sql.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class BanManager {

    private final DataSource dataSource;

    private HashMap<UUID, UltimateBanPlayer> bannedPlayers;
    private HashMap<String, UltimateBanPlayer> bannedPlayersByName;

    int nextId = 1;

    public BanManager(DataSource dataSource) {
        this.dataSource = dataSource;
        this.bannedPlayers = new HashMap<>();
        this.bannedPlayersByName = new HashMap<>();
        setupDatabase();
        loadBans();
    }

    public Optional<UltimateBanPlayer> find(UUID uuid) {
        return Optional.ofNullable(bannedPlayers.get(uuid));
    }

    public Optional<UltimateBanPlayer> findByName(String name) {
        return Optional.ofNullable(bannedPlayersByName.get(name));
    }

    public Set<String> getBannedPlayers() {
        return bannedPlayersByName.keySet();
    }

    public Ban applyBan(OfflinePlayer player, long until, String reason, String appliedBy) {
        Optional<UltimateBanPlayer> optional = find(player.getUniqueId());
        UltimateBanPlayer ultimateBanPlayer;

        if (!optional.isPresent()) {
            ultimateBanPlayer = new UltimateBanPlayer(player.getUniqueId(), player.getName());
            bannedPlayers.put(player.getUniqueId(), ultimateBanPlayer);
            bannedPlayersByName.put(player.getName(), ultimateBanPlayer);
            ConcurrentTask.runAsync(() -> {
                try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement("REPLACE INTO `ub_players` VALUES (?, ?)")) {
                    stmt.setString(1, player.getUniqueId().toString());
                    stmt.setString(2, player.getName());
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } else ultimateBanPlayer = optional.get();

        Ban ban = new Ban(nextId++, until, System.currentTimeMillis(), reason, appliedBy);
        ultimateBanPlayer.getBanHistory().add(0, ban);
        ConcurrentTask.runAsync(() -> {
            try (
                    Connection connection = dataSource.getConnection();
                    PreparedStatement stmt = connection.prepareStatement("INSERT INTO `ub_bans` VALUES (?, ?, ?, ?, ?, true, NULL, ?)", PreparedStatement.RETURN_GENERATED_KEYS)
            ) {
                stmt.setInt(1, ban.getId());
                if (until == -1)
                    stmt.setNull(2, Types.DATE);
                else
                    stmt.setDate(2, new Date(until));
                stmt.setDate(3, new Date(System.currentTimeMillis()));
                stmt.setString(4, reason);
                stmt.setString(5, appliedBy);
                stmt.setString(6, player.getUniqueId().toString());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return ban;
    }

    public void unban(Ban ban, Player staff) {
        ban.setActive(false);
        ban.setUnbannedBy(staff.getName());
        ConcurrentTask.runAsync(() -> {
            try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement("UPDATE `ub_bans` SET `active`=?, unbanned_by=? WHERE id=?")) {
                stmt.setBoolean(1, false);
                stmt.setString(2, staff.getName());
                stmt.setInt(3, ban.getId());
                stmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private void setupDatabase() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement createPlayersTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `ub_players` (`uuid` CHAR(36) PRIMARY KEY, `name` VARCHAR(16) NOT NULL)");
            createPlayersTable.executeUpdate();
            createPlayersTable.close();

            PreparedStatement createBansTable = connection.prepareStatement("CREATE TABLE IF NOT EXISTS `ub_bans` (" +
                    "`id` INT PRIMARY KEY AUTO_INCREMENT," +
                    "`until` DATETIME NULL," +
                    "`when` DATETIME NULL," +
                    "`reason` VARCHAR(100) NULL," +
                    "`applied_by` VARCHAR(16) NOT NULL," +
                    "`active` TINYINT NOT NULL DEFAULT 1," +
                    "`unbanned_by` VARCHAR(16) NULL DEFAULT NULL," +
                    "`player` CHAR(36) NOT NULL," +
                    "FOREIGN KEY (`player`) REFERENCES `ub_players`(`uuid`)" +
                    ")"
            );
            createBansTable.executeUpdate();
            createBansTable.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadBans() {
        try (Connection connection = dataSource.getConnection(); PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `ub_players`");) {
            ResultSet playersSet = stmt.executeQuery();
            while (playersSet.next()) {
                UltimateBanPlayer player = new UltimateBanPlayer(UUID.fromString(playersSet.getString("uuid")), playersSet.getString("name"));
                PreparedStatement bansStatement = connection.prepareStatement("SELECT * FROM `ub_bans` WHERE `player`=? ORDER BY `when` DESC");
                bansStatement.setString(1, player.getUUID().toString());

                ResultSet bansSet = bansStatement.executeQuery();
                while (bansSet.next()) {
                    int id = bansSet.getInt("id");
                    if (nextId < id) nextId = id;

                    Date until = bansSet.getDate("until");
                    player.getBanHistory().add(new Ban(
                            id,
                            until == null ? -1 : until.getTime(),
                            bansSet.getDate("when").getTime(),
                            bansSet.getString("reason"),
                            bansSet.getString("applied_by"),
                            bansSet.getBoolean("active"),
                            bansSet.getString("unbanned_by")
                    ));
                }

                bannedPlayers.put(player.getUUID(), player);
                bannedPlayersByName.put(player.getName(), player);
            }
            playersSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveAll() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement playersStmt = connection.prepareStatement("REPLACE INTO `ub_players` VALUES (?, ?)"),
                    bansStmt = connection.prepareStatement("REPLACE INTO `ub_bans` VALUES (?, ?, ?, ?, ?, ?, ?, ?)");

            for (UltimateBanPlayer player : bannedPlayers.values()) {
                playersStmt.setString(1, player.getUUID().toString());
                playersStmt.setString(2, player.getName());

                for (Ban ban : player.getBanHistory()) {
                    bansStmt.setInt(1, ban.getId());
                    if (ban.getUntil() == -1)
                        bansStmt.setNull(2, Types.DATE);
                    else
                        bansStmt.setDate(2, new Date(ban.getUntil()));
                    bansStmt.setDate(3, new Date(ban.getWhen()));
                    bansStmt.setString(4, ban.getReason());
                    bansStmt.setString(5, ban.getAppliedBy());
                    bansStmt.setBoolean(6, ban.isActive());
                    bansStmt.setString(7, ban.getUnbannedBy());
                    bansStmt.setString(8, player.getUUID().toString());
                    bansStmt.addBatch();
                }

                playersStmt.addBatch();
            }
            playersStmt.executeBatch();
            playersStmt.close();
            bansStmt.executeBatch();
            bansStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}