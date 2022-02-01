package me.frxq15.cllevels.SQLManagement;

import me.frxq15.cllevels.CLLevels;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    CLLevels plugin = CLLevels.getInstance();
    private final static Map<UUID, PlayerData> players = new HashMap<>();
    private final UUID uuid;
    private int level = 0;
    private int xp = 0;
    private Player lastkill = null;
    private boolean killcooldown = false;


    public PlayerData(UUID uuid) {
        this.uuid = uuid;
        players.put(uuid, this);
    }
    public void setLevel(int level) { this.level = level; }
    public void increaseLevel(int increase) { this.level = (this.level+increase); }
    public void setXP(int xp) {  this.xp = xp; }
    public void addXP(int xp) { this.xp = (this.xp+xp); }
    public void setKillCooldown(boolean type) { this.killcooldown = type; }
    public void setLastKill(Player p) { this.lastkill = p; }
    public int getLevel() { return level;  }
    public int getXP() { return xp; }
    public Player getLastKill() { return lastkill; }
    public boolean hasKillCooldown() { return killcooldown; }

    public static PlayerData getPlayerData(CLLevels plugin, UUID uuid) {
        if (!players.containsKey(uuid)) {
            PlayerData playerData = new PlayerData(uuid);
            playerData.setLevel(plugin.getSqlManager().getLevel(uuid));
            playerData.setXP(plugin.getSqlManager().getXP(uuid));
        }
        return players.get(uuid);
    }

    public void updateSQL() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getSqlManager().setLevel(this.uuid, level);
            plugin.getSqlManager().setXP(this.uuid, xp);
        });
    }

    public static Map<UUID, PlayerData> getAllPlayerData() {
        return players;
    }
    public static void removePlayerData(UUID uuid) { players.remove(uuid); }
}
