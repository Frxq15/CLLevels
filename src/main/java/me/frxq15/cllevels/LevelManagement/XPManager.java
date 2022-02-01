package me.frxq15.cllevels.LevelManagement;

import me.frxq15.cllevels.CLLevels;
import me.frxq15.cllevels.SQLManagement.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class XPManager implements Listener {
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if(e.getBlock().getType() == Material.AIR) return;
        PlayerData playerData = PlayerData.getPlayerData(CLLevels.getInstance(), e.getPlayer().getUniqueId());
        CLLevels.getLevelsFile().getConfigurationSection("XP_MANAGER.BLOCKS").getKeys(false).forEach(block -> {
            if(e.getBlock().getType().toString().equalsIgnoreCase(block)) {
                int xp = CLLevels.getLevelsFile().getInt("XP_MANAGER.BLOCKS."+e.getBlock().getType());
                playerData.addXP(xp);
                CLLevels.getInstance().getlManager().checkForLevelup(e.getPlayer());
                if(CLLevels.getLevelsFile().getBoolean("XP_MANAGER.SOUND_EFFECT")) {
                    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
                }
            }
        });
    }
    @EventHandler
    public void onMobKiller(EntityDeathEvent e) {
        if(!(e.getEntity().getKiller() instanceof Player)) return;
        Player p = e.getEntity().getKiller();
        PlayerData playerData = PlayerData.getPlayerData(CLLevels.getInstance(), p.getUniqueId());
        CLLevels.getLevelsFile().getConfigurationSection("XP_MANAGER.BLOCKS").getKeys(false).forEach(entity -> {
            if(e.getEntity().getType().name().equalsIgnoreCase(entity)) {
                int xp = CLLevels.getLevelsFile().getInt("XP_MANAGER.MOBS."+e.getEntity().getType().name());
                playerData.addXP(xp);
                CLLevels.getInstance().getlManager().checkForLevelup(p);
                if(CLLevels.getLevelsFile().getBoolean("XP_MANAGER.SOUND_EFFECT")) {
                    p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
                }
            }
        });
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        if(e.getEntity().getKiller() == null) return;
        Player p = e.getEntity().getKiller();
        Player target = e.getEntity();
        PlayerData playerData = PlayerData.getPlayerData(CLLevels.getInstance(), p.getUniqueId());
        if(playerData.hasKillCooldown()) {
            if(playerData.getLastKill() == target) { return; }
        }
        int xp = CLLevels.getLevelsFile().getInt("XP_MANAGER.PLAYERS");
        playerData.addXP(xp);
        CLLevels.getInstance().getlManager().checkForLevelup(p);
        if(CLLevels.getLevelsFile().getBoolean("XP_MANAGER.SOUND_EFFECT")) {
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 5, 1);
        }
        playerData.setLastKill(target);
        playerData.setKillCooldown(true);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(CLLevels.getInstance(), new Runnable(){
            @Override
            public void run(){
                if(p != null) {
                    playerData.setKillCooldown(false);
                    playerData.setLastKill(null);
                }
            }
        }, 20L * 600);
    }
}
