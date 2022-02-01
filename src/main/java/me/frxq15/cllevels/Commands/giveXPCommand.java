package me.frxq15.cllevels.Commands;

import me.frxq15.cllevels.CLLevels;
import me.frxq15.cllevels.SQLManagement.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class giveXPCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("cllevels.givexp")) {
            sender.sendMessage(CLLevels.formatMsg("NO_PERMISSION"));
            return true;
        }
        if(args.length == 2) {
            CommandSender s = sender;
            Player target = Bukkit.getPlayer(args[0]);
            int amount;
            try {
                Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                s.sendMessage(CLLevels.formatMsg("INVALID_INTEGER"));
                return true;
            }
            amount = Integer.parseInt(args[1]);
            if(target == null) {
                s.sendMessage(CLLevels.formatMsg("PLAYER_NOT_FOUND"));
                return true;
            }
            PlayerData playerData = PlayerData.getPlayerData(CLLevels.getInstance(), target.getUniqueId());
            playerData.addXP(amount);
            CLLevels.getInstance().getlManager().checkForLevelup(target);
            s.sendMessage(CLLevels.formatMsg("XP_GIVEN").replace("%target%", target.getName()).replace("%amount%", amount+""));
            return true;
        }
        sender.sendMessage(CLLevels.colourize("&cUsage: /givexp <player> <amount>"));
        return true;
    }
}
