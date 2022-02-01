package me.frxq15.cllevels.LevelManagement;

import me.frxq15.cllevels.CLLevels;
import me.frxq15.cllevels.SQLManagement.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class LManager implements Listener {
    CLLevels plugin = CLLevels.getInstance();
    @EventHandler
    public void onXPGain(PlayerExpChangeEvent e) {
        e.setAmount(0);
    }
    public void applyLevelup(Player p, int amount) {
        PlayerData playerData = PlayerData.getPlayerData(plugin, p.getUniqueId());
        if((playerData.getLevel()+amount)>CLLevels.getLevelsFile().getInt("MAX_LEVEL")) {
            plugin.log("Failed attempting to apply levelup ("+p.getName()+","+(amount+CLLevels.getLevelsFile().getInt("MAX_LEVEL"))+") this would exceed the maximum level.");
            return;
        }
        playerData.increaseLevel(amount);
        int level = playerData.getLevel();
        p.setLevel(level);
        for(String lines : CLLevels.getLevelsFile().getStringList("ON_LEVELUP."+level+".MESSAGE")) {
            p.sendMessage(CLLevels.colourize(lines).replace("%xpneeded%", plugin.getlManager().getRequiredXP(p)+""));
        }
        for(String command : CLLevels.getLevelsFile().getStringList("ON_LEVELUP."+level+".COMMANDS")) {
            command = command.replace("%player%", p.getName());
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
        if(CLLevels.getLevelsFile().getBoolean("ON_LEVELUP."+level+".FIREWORKS")) {
            runFireWorkTimer(p);
        }
    }
    public void runFireWorkTimer(Player p) {
        final int[] count = {5};
        BukkitTask bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (count[0] == 0) {
                    cancel();
                } else {
                    p.getWorld().spawn(p.getLocation(), Firework.class);
                    count[0]--;
                }
            }
        }.runTaskTimer(plugin, 5L, 5L);
    }
    public int getRequiredXP(Player p) {
        PlayerData playerData = PlayerData.getPlayerData(plugin, p.getUniqueId());
        int level = playerData.getLevel();
        if(level < CLLevels.getLevelsFile().getInt("MAX_LEVEL")) {
            int required = CLLevels.getLevelsFile().getInt("XP_REQUIRED."+(level+1));
            return required;
        }
        return 0;
    }
    public void checkForLevelup(Player p) {
        PlayerData playerData = PlayerData.getPlayerData(plugin, p.getUniqueId());
        int level = playerData.getLevel();
        int xp = playerData.getXP();
        int required = getRequiredXP(p);

        if(level < CLLevels.getLevelsFile().getInt("MAX_LEVEL")) {
            if(xp >= required) {
                applyLevelup(p, 1);
                checkForLevelup(p);
                return;
            }
        }
        return;
    }
}
