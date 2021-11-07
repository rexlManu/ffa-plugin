/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.command;

import de.ffapvp.freeforall.FreeForAllPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ForceMapCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.hasPermission("ffa.forcemap")) {
            commandSender.sendMessage(FreeForAllPlugin.PREFIX + "§7Dazu hast du §akeine§7 Berechtigung.");
            return true;
        }


        return false;
    }
}
