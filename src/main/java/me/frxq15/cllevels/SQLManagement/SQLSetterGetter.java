package me.frxq15.cllevels.SQLManagement;

import me.frxq15.cllevels.CLLevels;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLSetterGetter {
    static CLLevels plugin = CLLevels.getInstance();

    public static boolean playerExists(UUID uuid) {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());

            ResultSet results = statement.executeQuery();
            if (results.next()) {
                return true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void updatePlayerName(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement selectPlayer = plugin.getConnection().prepareStatement("SELECT * FROM `" + plugin.table + "` WHERE uuid = ?;");
                selectPlayer.setString(1, player.getUniqueId().toString());
                ResultSet playerResult = selectPlayer.executeQuery();

                if (playerResult.next() && !playerResult.getString("player").equals(player.getName())) {
                    PreparedStatement updateName = plugin.getConnection().prepareStatement("UPDATE `" + plugin.table + "` SET player = ? WHERE uuid = ?;");
                    updateName.setString(1, player.getName());
                    updateName.setString(2, player.getUniqueId().toString());
                    updateName.executeUpdate();
                }

                playerResult.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public void createTable(String table) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                PreparedStatement statement = plugin.getConnection().prepareStatement(
                        "CREATE TABLE IF NOT EXISTS `" + table + "` (uuid VARCHAR(36) PRIMARY KEY, player VARCHAR(16), level INT(11), xp INT(11));");
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    public void createPlayer(final UUID uuid, String name) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            if (!playerExists(uuid)) {
                PreparedStatement insert = plugin.getConnection()
                        .prepareStatement("INSERT INTO " + plugin.table + "(uuid,player,level,xp) VALUES (?,?,?,?)");
                insert.setString(1, uuid.toString());
                insert.setString(2, name);
                insert.setInt(3, 0);
                insert.setInt(4, 0);
                insert.executeUpdate();

            }
        } catch (SQLException e) {
            e.printStackTrace();
            }
        });
    }
    public void setLevel(UUID uuid, int level) {
        if(!playerExists(uuid)) {
            plugin.log("Error whilst setting level for uuid "+uuid+", please contact the developer about this error.");
            return;
        }
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET level=? WHERE UUID=?");
            statement.setInt(1, level);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setXP(UUID uuid, int xp) {
        if(!playerExists(uuid)) {
            plugin.log("Error whilst setting xp for uuid "+uuid+", please contact the developer about this error.");
            return;
        }
        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("UPDATE " + plugin.table + " SET xp=? WHERE UUID=?");
            statement.setInt(1, xp);
            statement.setString(2, uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getLevel(UUID uuid) {
        if(!playerExists(uuid)) {
            plugin.log("Error whilst getting level for uuid "+uuid+", please contact the developer about this error.");
            return 0;
        }

        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getInt("level");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public int getXP(UUID uuid) {
        if(!playerExists(uuid)) {
            plugin.log("Error whilst getting level for xp "+uuid+", please contact the developer about this error.");
            return 0;
        }

        try {
            PreparedStatement statement = plugin.getConnection().prepareStatement("SELECT * FROM " + plugin.table + " WHERE UUID=?");
            statement.setString(1, uuid.toString());
            ResultSet results = statement.executeQuery();
            results.next();
            return results.getInt("xp");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
