/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.task;

import de.ffapvp.freeforall.FreeForAllPlugin;
import de.ffapvp.freeforall.listener.FreeForAllListener;
import de.ffapvp.freeforall.mode.map.Map;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.github.paperspigot.Title;

@Getter
public class MapchangeTask extends Task {

    private int seconds;

    public MapchangeTask() {
        this.runTaskTimerAsynchronously(FreeForAllPlugin.getPlugin(), 0, 20);
        this.seconds = 10 * 60;
    }

    @Override
    public void run() {
        if (seconds <= 0) {
            this.trigger();
            return;
        }
        this.seconds--;
    }

    public void trigger(Map map) {
        Bukkit.getOnlinePlayers().forEach(o -> {
            FreeForAllPlugin.getPlugin().getPlayerScoreboard().updateScoreboard(o);
            o.teleport(FreeForAllPlugin.getPlugin().getMap().getLocation("spawn"));
            o.getInventory().clear();
            o.getInventory().setArmorContents(null);
            o.setHealth(o.getMaxHealth());
            o.setLevel(0);
            o.setExp(0);
            o.sendTitle(Title.builder().title("§r").subtitle(FreeForAllPlugin.PREFIX + map.getName()).build());
            o.playSound(o.getLocation(), Sound.LEVEL_UP, 1f, 1f);
            FreeForAllListener.spawnZoneEnter(o);
        });
        FreeForAllPlugin.getPlugin().setMap(map);
        this.seconds = 10 * 60 + 1;
    }

    public void trigger() {
        this.trigger(FreeForAllPlugin.getPlugin().getMapHandler().getRandomMap());
    }
}
