package com.flame.api.menu.command;

import com.flame.api.menu.holder.MenuManagerHolder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * author : s0ckett
 * date : 23.06.25
 */

public class MenuCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cЭта команда только для игроков!");
            return true;
        }

        MenuManagerHolder.get().openMenu(player, "example");
        return true;
    }
}
