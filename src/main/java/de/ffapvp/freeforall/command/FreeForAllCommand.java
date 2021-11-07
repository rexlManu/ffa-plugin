/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.command;

import de.ffapvp.freeforall.FreeForAllPlugin;
import de.ffapvp.freeforall.mode.map.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FreeForAllCommand implements CommandExecutor {

    private List<SubCommand> commands;

    public FreeForAllCommand() {
        this.commands = new ArrayList<>();
        this.commands.add(SubCommand.of(1, "help", (player, args) -> {
            player.sendMessage("ffa createmap <name>");
            player.sendMessage("ffa set <map> <name>");
        }));
        this.commands.add(SubCommand.of(2, "set", (player, strings) -> {
            FreeForAllPlugin.getPlugin().getLocationConfiguration().set(strings[1], player.getLocation());
            FreeForAllPlugin.getPlugin().saveLocationConfiguration();
            player.sendMessage(strings[1] + " gesetzt.");
        }));
        this.commands.add(SubCommand.of(2, "createmap", (player, args) -> {
            FreeForAllPlugin.getPlugin().getMapHandler().addMap(args[1]);
            player.sendMessage(args[1] + " map created");
        }));
        this.commands.add(SubCommand.of(3, "set", (player, args) -> {
            Map map = FreeForAllPlugin.getPlugin().getMapHandler().getMapByName(args[1]);
            if (map == null){
                player.sendMessage(args[1] + " map not found");
                return;
            }
            map.getConfiguration().set(args[2], player.getLocation());
            map.saveConfiguration();
            player.sendMessage(args[2] + " gesetzt.");
        }));
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.hasPermission("ffa")) return true;
        Player player = (Player) commandSender;
        for (int i = 0; i < args.length; i++) {
            for (SubCommand subCommand : commands) {
                if (subCommand.getLength() == (i + 1)) {
                    if (subCommand.getArgument().equals(args[0].toLowerCase())) {
                        subCommand.consumer.accept(player, args);
                        break;
                    }
                }
            }
        }
        return true;
    }

    @Data
    @AllArgsConstructor
    public static class SubCommand {

        private int length;
        private String argument;
        private BiConsumer<Player, String[]> consumer;

        public static SubCommand of(int length, String argument, BiConsumer<Player, String[]> consumer) {
            return new SubCommand(length, argument, consumer);
        }
    }
}
