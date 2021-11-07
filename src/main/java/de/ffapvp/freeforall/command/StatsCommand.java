/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.command;

import de.dytanic.cloudnet.api.CloudAPI;
import de.ffapvp.freeforall.FreeForAllPlugin;
import de.ffapvp.freeforall.stats.GlobalPlayerStats;
import de.ffapvp.freeforall.stats.StatsHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            this.printStats(((Player) commandSender).getUniqueId());
            return true;
        } else if (strings.length == 1) {
            UUID playerUniqueId = CloudAPI.getInstance().getPlayerUniqueId(strings[0]);
            if (playerUniqueId == null) {
                commandSender.sendMessage(FreeForAllPlugin.PREFIX + "§7Dieser Spieler befindet sich nicht in unserer Datenbank.");
                return true;
            }
            this.printStats(playerUniqueId);
            return true;
        } else {
            commandSender.sendMessage(FreeForAllPlugin.PREFIX + "§7Verwendung: §8/§7stats §8[§aSpielername§8]");
        }
        return false;
    }

    private void printStats(UUID uuid) {
        StatsHandler statsHandler = FreeForAllPlugin.getPlugin().getStatsHandler();
        GlobalPlayerStats globalStats = statsHandler.getGlobalStatsByPlayer(uuid);
        if (globalStats == null) {
            globalStats = statsHandler.loadGlobalStats(uuid, false);
            return;
        }
    }
}
