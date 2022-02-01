package me.frxq15.cllevels.Commands;

import me.frxq15.cllevels.CLLevels;
import me.frxq15.cllevels.SQLManagement.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class XPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] strings) {
        CLLevels plugin = CLLevels.getInstance();
        if(!(sender instanceof Player)) {
            plugin.log("This command cannot be executed from console.");
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("cllevels.xp")) {
            p.sendMessage(plugin.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(strings.length == 0) {
            PlayerData playerData = PlayerData.getPlayerData(plugin, p.getUniqueId());
            for(String lines : plugin.getConfig().getStringList("CURRENT_XP")) {
                p.sendMessage(CLLevels.colourize(lines).replace("%current%", playerData.getXP()+"").replace("%needed%", plugin.getlManager().getRequiredXP(p)+"")
                        .replace("%player%", p.getName()));
            }
            return true;
        }
        if(strings.length == 1) {
            if(!p.hasPermission("cllevels.xp.others")) {
                p.sendMessage(CLLevels.formatMsg("NO_PERMISSION"));
                return true;
            }
            Player target = Bukkit.getPlayer(strings[0]);

            if(target == null) {
                p.sendMessage(CLLevels.formatMsg("PLAYER_NOT_FOUND"));
                return true;
            }
            PlayerData playerData = PlayerData.getPlayerData(plugin, target.getUniqueId());
            for(String lines : plugin.getConfig().getStringList("CURRENT_XP")) {
                p.sendMessage(CLLevels.colourize(lines).replace("%current%", playerData.getXP()+"").replace("%needed%", plugin.getlManager().getRequiredXP(target)+"")
                        .replace("%player%", target.getName()));
            }
            if(!target.isOnline()) {
                PlayerData.removePlayerData(target.getUniqueId());
            }
            return true;
        }
        p.sendMessage(plugin.colourize("&cUsage: &c/xp <player>"));
        return true;
    }
}
