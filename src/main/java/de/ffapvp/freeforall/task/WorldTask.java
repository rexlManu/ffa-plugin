/*
 * © Copyright - Emmanuel Lampe aka. rexlManu 2020.
 */
package de.ffapvp.freeforall.task;

import de.ffapvp.freeforall.FreeForAllPlugin;
import org.bukkit.Bukkit;

public class WorldTask extends Task{

    public WorldTask() {
        runTaskTimer(FreeForAllPlugin.getPlugin(), 0, 20);
    }

    @Override
    public void run() {
        Bukkit.getWorlds().forEach(world -> {
            world.setTime(2000);
        });
    }
}
